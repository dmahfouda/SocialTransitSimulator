class Population {
  processing.xml.XMLElement populationxml;
  Person[] person;
  int numPeople;


  /***************************
   * CONSTRUCTOR
   ***************************/
  Population (processing.xml.XMLElement popxml) {
    populationxml = popxml;   
    numPeople = populationxml.getChildCount();
    person = new Person[numPeople];
  }

  /***************************
   * POPULATE
   ***************************/
  void populate () {
    for (int i=0;i<numPeople;i++){ 
      processing.xml.XMLElement personxml = populationxml.getChild(i);
      processing.xml.XMLElement itineraryxml = personxml.getChild(0);
      person[i] = new Person(itineraryxml.getIntAttribute("start"),itineraryxml.getIntAttribute("destination"), i);
    }
  }

  /***************************
   * REPOPULATE
   ***************************/
  void repopulate() { 
    for (int i=0;i<numPeople;i++){
      person[i].destination.arrived_passengers.remove(person[i]);  //will this work? does this work? 
      person[i].location = person[i].start;
      person[i].destination = person[i].destination;
      person[i].travel_distance = 0;
      person[i].arrived = false;
      person[i].in_line = false;
      person[i].in_car = false;  
    }

    for (int i=0;i<fleet.car.length;i++){
      fleet.car[i].travel_distance=0;
    }
  }

  //  void repopulate() { 
  //    int new_loc, new_dest, high_station;
  //    high_station = mnbprtodi.station.length -1;
  //    for (int i=0;i<populationxml.getChildCount();i++){
  //      new_loc = int(random(high_station));
  //      new_dest = int(random(high_station)); 
  //      person[i].destination.arrived_passengers.remove(person[i]);  //will this work? does this work? 
  //      person[i].location = mnbprtodi.station[new_loc];
  //      person[i].destination = mnbprtodi.station[new_dest];
  //      person[i].travel_distance = 0;
  //      person[i].arrived = false;
  //      person[i].in_line = false;
  //      person[i].in_car = false;  
  //    }
  //
  //    for (int i=0;i<fleet.car.length;i++){
  //      fleet.car[i].travel_distance=0;
  //    }
  //  }
}



