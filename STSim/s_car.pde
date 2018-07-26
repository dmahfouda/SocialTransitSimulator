class Car {
  float       orientation, x, y;
  ArrayList   cargo;
  Station     previous, at_station, next, destination, on_road, start; 
  Pathfinder  gps;
  boolean     stop;
  int         name;  
  float       travel_time,travel_distance,distance_to_destination, waste;

  int         capacity, speed; //constants

  int         pathmark; //counter for path

  /********************************************************************************************************
   * CAR_CONSTRUCTOR
   * [0]capacity  [1]start  [2]destination [3]speed  [4]behavior
   *********************************************************************************************************/
  Car (int cap, int star, int spe, int behav, int n) {                           
    name = n;
    capacity = cap;
    speed = spe; 
    waste = 0;

    //make room in car
    cargo = new ArrayList();   

    //place car on map at beginning of path
    previous = mnbprtodi.station[star];         
    x=previous.x;
    y=previous.y;
    start = previous;

    //this is just a convention for starting at a station
    //rather than on the road 
    next = previous;                                                

    //cars first destination is always 0
    destination = mnbprtodi.station[0];

    //give car a pathfinder
    gps = new Pathfinder(mnbprtodi.station);            
    gps.findpath(previous,destination);

    distance_to_destination = destination.distance_from_start;
    //set counter
    pathmark = 0;

    orientation = atan2(next.y-previous.y,next.x-previous.x);       
    //original orientation is the line between previous and next node;

    //this seems excessive - perhaps there should only be one
    //on_road station, rather than one for each c
    on_road = new Station (0,0,0,0);
    at_station = on_road;    //create a station for storing a state
    stop = true;
  }


  /********************************************************************************************************
   * NAVIGATE_FUNCTION
   * most navitation occurs in STOP and TURN
   *********************************************************************************************************/
  void navigate () {
//    println("car "+name+" navigates. speed: "+speed+" previous:"+previous.position+" next: "+next.position+" x="+x);
    if (speed==0) {
      read_beacon();
//      println("speed after beacon: "+speed);
    }
    if (stopShort) {  
      stop_short();
    }
//    println("speed after stopShort: "+speed);

    //if car is near next station  
    if (dist(x,y,next.x,next.y)<speed) {
      
      x = next.x;
      y = next.y; 

      stop();
      turn();
    } 
    else {                       
      follow_road();
      carry_cargo();
      at_station = on_road;
    }
  }


  /********************************************************************************************************
   * STOP_FUNCTION
   *********************************************************************************************************/
  void stop () {
    at_station = next; 


    /*********************************************************
     * print(name+" stops at station "+at_station.name+" with ");
     * for (int i=0;i<cargo.size();i++){
     * Person p = (Person)cargo.get(i);
     * print(p.name);
     * }
     * println();
     * println("car: "+name+" stops at: "+next.name);
     **********************************************************/

    at_station.car_queue = (Car[]) append(at_station.car_queue,this);
    while(stop) {
      at_station.passenger_exchange();
    }

    //pull away from station
    at_station.car_queue = (Car[]) subset(at_station.car_queue,1,at_station.car_queue.length-1);
    listen_to_passengers();

    stop = true;
  }                       

  /********************************************************************************************************
   * PASSENGER_WANTS_OUT_FUNCTION
   *********************************************************************************************************/
  boolean passenger_wants_out () {
    for(int i=0;i<cargo.size();i++){
      Person p = (Person) cargo.get(i);
      if (p.destination == next){
        return true;
      }
    }
    return false;
  }

  /********************************************************************************************************
   * FOLLOW_ROAD_FUNCTION
   *********************************************************************************************************/
  void follow_road() {
    //println("car "+name+" follows road. speed="+speed+" previous="+previous.name+" x="+x);
    float old_x =x;
    float old_y =y;
    x += cos(orientation)*speed;
    y += sin(orientation)*speed;
    float d = dist(old_x,old_y,x,y);
    waste += d*(capacity-cargo.size());
    //println ("car: "+name+" waste: "+waste); 
    distance_to_destination -= d;
//    println("car:"+name+" destination:"+destination.name+" distance to destination:"+distance_to_destination+"x: "+x+"y: "+y);    
    travel_distance += d; 
    total_waste[iterat] += d*(capacity-cargo.size());
  }


  /**********************************************************************************************************
   * CARRY_CARGO
   * updates x and y loc for passengers in cargo
   **********************************************************************************************************/
  void carry_cargo(){
    for (int i=0;i<cargo.size();i++){
      Person p = (Person) cargo.get(i);
      p.in_transit();
    }
  }


  /********************************************************************************************************
   * LISTEN_TO_PASSENGERS_FUNCTION
   *********************************************************************************************************/
  void listen_to_passengers(){
    //if there are passengers in the car 
    if (cargo.size() > 0) {
      destination = cb4();
      gps.findpath(next,destination);
      distance_to_destination = destination.distance_from_start;
      //we just reset path (so we should start at the beginning)
      pathmark = 0;
    } 
  }                                                                 

  /****************************************************************************************************
   * TURN_FUNCTION
   *****************************************************************************************************/
  void turn() {
    /******************************
     * print("car:"+name+" path:");
     * for(int i=0;i<gps.path.length;i++) {
     * print(gps.path[i].name);
     * }
     * print(" next: "+next.name);
     * print(" pathmark:"+pathmark);     
     * println();
     *******************************/
    previous = next;
    pathmark++;  
    //if there is a place to go
    if(pathmark <= gps.path.length-1){
      //if(gps.path[pathmark] != null) { 
      next = gps.path[pathmark];
    } 
    else { 
      speed=0;
      pathmark = 0;
    }
    for (int i=0;i<cargo.size();i++){
      Person p = (Person)cargo.get(i);
      p.requeue();
    }
    orientation = atan2(next.y-previous.y,next.x-previous.x);
  }


  /********************************************************************************************************
   * DISPLAY_FUNCTION
   *********************************************************************************************************/
  void display () {
    noStroke();
    fill(200,0,0,200);
    translate(x,y);
    rotate(orientation);  
    rectMode(CENTER);
    rect(0,0,10,5);
    resetMatrix();
    cargo_display();
  }


  /********************************************************************************************************
   * READ_BEACON_FUNCTION
   *********************************************************************************************************/
  void read_beacon() {
//    println("car "+name+" reads beacon. speed:"+speed+" previous:"+previous.position+" next: "+next.position+" x:"+x);
    //if someone wants a ride
    if (beacon.size() > 0) {
      float min_distance = 10000;
      float current_distance;
      Person nearest_person = (Person)beacon.get(0);
      Person current_person;    

      //find out who's nearest
      for (int i=0;i<beacon.size();i++){     
        current_person = (Person)beacon.get(i);
        //find the nearest passenger location
        current_distance = dist(this.x,this.y,current_person.location.x,current_person.location.y);
        if (current_distance<min_distance){
          min_distance = current_distance;
          nearest_person = current_person;
        }
      }
      
//      println("nearest person: "+nearest_person.name);
      //find location of nearest person
      destination = nearest_person.location;
      gps.findpath(next,destination);
      distance_to_destination = destination.distance_from_start;
      pathmark=0;

      //start car
      speed = fleet.speed;
//      println("car: "+name+", speed: "+speed);    
    }
  }

  /********************************************************************************************************
   * DISPLAY_CARGO_FUNCTION
   * //why doesn't this call person.display()?
   *********************************************************************************************************/
  void cargo_display () {
    for (int i=0;i<cargo.size();i++) {
      Person p = (Person)cargo.get(i);
      translate(-15,-1);
      p.display();
    }
    resetMatrix();
  }

  /***********************************************************************************************
   * BEHAVIOR 3
   * CB3 orients returns the cargo[].destination node whose Euclidean distance from the car is least.
   ************************************************************************************************/
  Station cb3 () {
    float min_distance = 10000;
    float current_distance;
    Person nearest_person = (Person)cargo.get(0);
    Person current_person;  
    for (int i=0;i<cargo.size();i++){       
      current_person = (Person)cargo.get(i);
      //find the nearest passenger destination
      current_distance = dist(this.x,this.y,current_person.destination.x,current_person.destination.y);
      if (current_distance<min_distance){
        min_distance = current_distance;
        nearest_person = current_person;
      }
    }
    return nearest_person.destination;
  }   

  /***********************************************************************************************
   * BEHAVIOR 4
   * CB4 orients returns the cargo[].destination node whose path distance from the car is least.
   ************************************************************************************************/
  Station cb4 () {
    float min_distance = 10000;
    float current_distance;
    Person nearest_person = (Person)cargo.get(0);
    Person current_person;  
    for (int i=0;i<cargo.size();i++){       
      current_person = (Person)cargo.get(i);
      //find the nearest passenger destination
      gps.findpath(next, current_person.destination);
      current_distance = current_person.destination.distance_from_start;
      if (current_distance<min_distance){
        min_distance = current_distance;
        nearest_person = current_person;
      }
    }
    return nearest_person.destination;
  } 

  /***********************************************************************************************
   * STOP_SHORT
   * this function should stop cars, if there is no need for them to operate
   ************************************************************************************************/
  void stop_short() {
    boolean this_car_is_farthest = false;
    int cars_running=0;
    for (int i=0;i<fleet.car.length;i++){
      if (fleet.car[i].speed > 0) {
        cars_running++;
//        println("cars_running: "+cars_running);
      }
      //if this car is farthest
      if (distance_to_destination>fleet.car[i].distance_to_destination) {
        this_car_is_farthest = true;
//        println("this car is furthest");
      }  
    }
    //if there are fewer people waiting than cars running and this car is the farthest from its destination
    //and this car is not carrying passengers
    if (beacon.size()<cars_running && cargo.size()==0 && this_car_is_farthest) {
      speed = 0;
    }
  }
}


