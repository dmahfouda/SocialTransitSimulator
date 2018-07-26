class Fleet {
  Car[] car;
  int numCars=0;
  int speed=20;
  int[] mappedTraits; 
  processing.xml.XMLElement fleetxml;

  /*****************************constructor********************************/
  Fleet (int[] mT) { 
    for (int i=0;i<mT.length;i++) {
      //build cars only if they have capacity
      if (mT[i] != 0) { 
        numCars++;
      }
    }
    car = new Car[numCars];
    mappedTraits = mT;
  }
  
 /******************************alternate constructor*********************/ 
   Fleet (processing.xml.XMLElement flexml) { 
    fleetxml = flexml;
    car = new Car[fleetxml.getChildCount()];
    numCars = fleetxml.getChildCount();
  }

  /*****************************buildCars********************************/
  void build_cars () {
    for (int i=0;i<numCars;i++) {
      // Car (int cap, int star, int spe, int behav, int n)
      //make start argument random   
      car[i] = new Car(mappedTraits[i],90,0,1,i);
    }
  }
  
  /*****************************buildCars1********************************/
  void build_cars1 () {
    for (int i=0;i<fleetxml.getChildCount();i++) {
      processing.xml.XMLElement carxml = fleetxml.getChild(i);
      car[i] = new Car(carxml.getIntAttribute("capacity"), carxml.getIntAttribute("location"), carxml.getIntAttribute("speed"),1,i);
    }
  }
  
}


