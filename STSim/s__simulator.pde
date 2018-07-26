class Simulator {

  PImage      mapp;

  int         counter;
  int[]       results;
  int[]       mappedTraits;

  boolean     repopulated;
  boolean     display = false;
  boolean     drawing;
  int         fitness;
  float       fitness_constant = 10000;

  /*****************************constructor********************************/
  Simulator(int[] mT) {
    fitness = 0;
    counter = 0;
//    fdisplay();
    //place this in setup function 
    mappedTraits = mT;
    simulation_constructor();
    setInitials();
  }


  /*****************************run/(draw)*******************************/
  int run () {
    while (drawing) {
//      println("drawing");  
      controller();
      if (repopulated) {
//        println("repopulated");
        for (int i=0;i<population.populationxml.getChildCount();i++) {
          population.person[i].queue();                      
        }
        delay(50);//to load xml file
        repopulated = false;
      }

//      for(int i=0;i<mnbprtodi.numStations;i++) {
//        if (display) {
//          mnbprtodi.station[i].display();
//        }
//      }  

      //car methods are placed in between person methods so poeple can be visible over cars
      for (int i=0;i<fleet.car.length;i++){
        fleet.car[i].navigate();
//        println("navigate");
//        if (display) {
//          fleet.car[i].display();
//        }
      }
      
//      println("arrived list: "+arrived_list);
      if (arrived_list == population.person.length) { 
        iterat++;
        if (iterat == iterations){  
          break;
        }
      }

      if (iterat < iterations){
        time[iterat]++;
      }
    }
    
    float total_vehicle_cost = 0;
    float total_travel_time = 0;
    float total_waste = 0;
    
    for (int i=0;i<population.person.length;i++) {
      total_travel_time += population.person[i].travel_end - population.person[i].wait_start; 
    }
    
    for (int i=0;i<fleet.car.length;i++) {
      total_waste += fleet.car[i].waste; 
      total_vehicle_cost += fleet.car[i].capacity*100;
    }
    
    fitness = floor(fitness_constant - (total_travel_time+total_waste+total_vehicle_cost));
    
    defaultsSet = false;
    
    return fitness;
  }

  /*****************************display********************************/
//  void fdisplay () {
//    if (display) {
//      mapp = loadImage("home_roads.jpg");
//      size (mapp.width,mapp.height);
//
//      //print(mapp.width+" "+mapp.height);
//      image(mapp,0,0); 
//      font = loadFont("AlBayan-10.vlw"); 
//      textFont(font, 10);
//    } 
//  }

  /*****************************setInitials********************************/
  void setInitials () {
    iterat = 0;
    iterations = 1;
    repopulated=true;
//    beacon = new ArrayList();
    arrived_list = 0; 
    time = new long[iterations];
    for (int i=0;i<iterations;i++){
      time[i]=0;
    }  
    results = new int[2];
    drawing = true;
  }

  /*****************************simulationConstructor********************************/
  void simulation_constructor() {
    if (!defaultsSet) {
//    processing.xml.XMLElement mapDataXML;
//    mapDataXML = new processing.xml.XMLElement(this, mapDataFile);
    mnbprtodi = new MNBPRTODI(mapDataXML);
    mnbprtodi.connectStations();
    
//    processing.xml.XMLElement populationDataXML;
//    populationDataXML = new processing.xml.XMLElement(this, populationDataFile);
    population = new Population(populationDataXML);
    population.populate();
    }
    
    fleet = new Fleet(mappedTraits);
    fleet.build_cars();
  }


//  /*****************************interrupts********************************/
//  void keyPressed () {
//    if (key == 'p') {
//      noLoop();
//    }
//    else {
//      loop();
//    }
//  }
}



