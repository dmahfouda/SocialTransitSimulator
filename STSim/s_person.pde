class Person {
  Station start, location, destination;
  int name;
  float x,y;
  Car car;
  boolean in_line,in_car,arrived;
  float travel_distance;
  long travel_start,travel_end,wait_start,wait_end;
  Pathfinder gps;

  /***************************
   * CONSTRUCTOR
   ***************************/
  Person (int loc, int dest, int n){
    start = location = mnbprtodi.station[loc];
    destination = mnbprtodi.station[dest];

    x = location.x;
    y = location.y;

    name = n;

    in_car = false;
    in_line = false;
    arrived = false;

    travel_distance = 0;

    //Personal GPS
    gps = new Pathfinder(mnbprtodi.station);            
  }

  /***************************
   * QUEUE
   ***************************/
  void queue(){
    if (location != destination && !in_line){                                
      location.queue = (Person[])append(location.queue, this);                         //i get in line
      start=location;
      in_line = true;
      x = location.x;
      y = location.y;  
      //call a cab
//      println("person "+name+" queues at station "+location.name);
      beacon.add(this);
      /**********************************
        print("(add)beacon: "); 
        for (int i=0;i<beacon.size();i++){
        Person p = (Person)beacon.get(i);
        print (p.location.name+" ");
        }
        println(); 
       **********************************/
//      wait_start = time[iterat];
    }
    else {
      if (location == destination && arrived != true) {
        x = destination.x;
        y = destination.y;   
        start = location; //if passenger is already arrived;
//        wait_start = wait_end = travel_start = travel_end = time[iterat];
        destination.arrived_passengers.add(this);    
        arrived = true;
//        arrived_list++;
      }       
    } 
  }

  /***************************
   * REQUEUE
   ***************************/
  void requeue () {
    gps.findpath(car.previous,destination);
    float d1 = gps.station[destination.position].distance_from_start;
    gps.findpath(car.next,destination);
    float d2 = gps.station[destination.position].distance_from_start;
    //if previous is closer than next
    if (d1<d2){
      //get out of car
      car.cargo.remove(this); 

      //get in line;
      location.queue = (Person[])append(location.queue, this);
      in_line = true;
      x = location.x;
      y = location.y;  
      //call a cab
      beacon.add(this);
    }
  } 


  /***************************
   * BOARD
   ***************************/
  void board(){
    location.queue = (Person[])subset(location.queue, 1, location.queue.length-1);      //i get off line      
    car.cargo.add(this);                                                                //i get in car
    travel_start = wait_end = time[iterat];

    //i stop calling a cab
    if (beacon.contains(this)) {
      beacon.remove(this);
      /**************************************
       * print(name);
       * print("(sub)beacon: "); 
       * for (int i=0;i<beacon.size();i++){
       * Person p = (Person)beacon.get(i);
       * print(p.name+""+p.location.name+" ");      
       * }
       * println();  
       *****************************************/
    }
    in_line = false;  
  }  

  /***************************
   * IN_TRANSIT
   ***************************/
  void in_transit(){
    travel_distance += dist(x,y,car.x,car.y);
    x = car.x;                     
    y = car.y;         
  }

  //check station needs to return a boolean so exit_procedure()
  //can account for cargos ArrayList decreasing by 1 aft
  boolean check_station(){
    /*********************************
     * println(name+" checks station");
     **********************************/
    if (location.name == destination.name){
      arrive();
      return true;
    }
    else {
      return false;
    }
  }



  /***************************
   * ARRIVE
   ***************************/
  void arrive(){
    car.cargo.remove(this); 
    //these declarations may not be neccessary 
    x = destination.x;
    y = destination.y;   
    destination.arrived_passengers.add(this);    
    travel_end = time[iterat];
    arrived = true;
    arrived_list++;
//    println(name+" arrives");
    total_travel_time[iterat]+=travel_end;
  }


  /***************************
   * DISPLAY
   ***************************/
  void display(){
    if (arrived){
      fill(0,200,100);
    } 
    else {
      fill (250,150,0);
    }
    noStroke();                        
    ellipse(x,y,7,7);
    if (!location.connected) {
      fill(0);
      text(location.position,x+5,y);    
    }
  }
}


