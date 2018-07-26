class Station {
  int name;
  int position;
  float x,y;
  Person[] queue;
  Car[] car_queue;
  Station[] linked_to_station;
  float[] distance_to_station;
  float distance_from_start;
  Station previous;
  boolean visited;
  ArrayList arrived_passengers;
  boolean connected;

  /***************************
   * STATION CONSTRUCTOR
   ***************************/
  Station(int n, float xx, float yy, int p /*Station[] links*/) { 
    position = p;
    x = xx;
    y = yy;
    name = n;
    connected = false;
    queue = new Person[0];
    car_queue = new Car[0];
    linked_to_station = new Station[0];
    distance_to_station = new float[0];
    arrived_passengers = new ArrayList();
  }

  /***************************
   * PASSENGER EXCHANGE
   ***************************/
  void passenger_exchange(){
    exit_procedure();
    board_procedure();
  }

  /***************************
   * EXIT_PROCEDURE
   ***************************/
  void exit_procedure() {
    //for every passenger in first car
    for (int i=0;i<car_queue[0].cargo.size();i++) { 
      //call this passenger "p"            
      Person p = (Person)car_queue[0].cargo.get(i);                    
      //change p's location to this station 
      p.location = this;   
      //p checks to see if he should get out      
      if (p.check_station()){
        //to point to remaining passengers
        i--;
      }        
    }      
  }

  /***************************
   * BOARD_PROCEDURE
   ***************************/
  void board_procedure() {
    //while there is room in the car and people in line
    while ((car_queue[0].cargo.size() < car_queue[0].capacity) &&     
      (queue.length > 0)){
      //first person in line looks at first car in line;      
      queue[0].car = car_queue[0];                                  
      queue[0].board();                                     
    }   
    //car at front of line can leave      
    car_queue[0].stop = false;                                            
  }

  /***************************
   * DISPLAY
   ***************************/
  void display(){
    if (connected) {
    fill(0,0,100,200);
    noStroke();
    triangle(x-2,y-2,x,y+2,x+2,y-2);
    fill(0); 
    text(position,x+4,y);
    queue_display();
    link_display();
    arrived_display();
    }
  }

  /***************************
   * QUEUE DISPLAY 
   ***************************/
  void queue_display(){
    for (int i=0;i<queue.length;i++) {
      translate(0,10);
      queue[i].display();
    }
    resetMatrix();
  }

  /***************************
   * ARRIVED DISPLAY
   ***************************/
  void arrived_display() {
    for (int i=0;i<arrived_passengers.size();i++){
      translate(0,-10);
      Person p = (Person)arrived_passengers.get(i);
      p.display();
    }
    resetMatrix();
  }

  /***************************
   * LINK DISPLAY
   ***************************/
  void link_display() {
    strokeWeight(1);
    stroke(153);
    for(int i=0;i<linked_to_station.length;i++) {
      line(this.x,this.y,linked_to_station[i].x,linked_to_station[i].y);
      float arrowx = this.x - .75*(this.x-linked_to_station[i].x);
      float arrowy = this.y - .75*(this.y-linked_to_station[i].y);
      fill(230);
      ellipse(arrowx,arrowy,5,5);
    }
  }
}

