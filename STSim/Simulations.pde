class Simulations {
  processing.xml.XMLElement simulationXML;
  Simulation[] simulation;
  int numSimulations;
  float screenW;

  Simulations ( ) {
    numSimulations = 0;
    simulation = new Simulation[numSimulations];
    screenW = screen.width - 220;
  }

  Simulations (processing.xml.XMLElement simXML) {
    simulationXML = simXML;
    numSimulations = simulationXML.getChildCount();
    println("numSimulations: "+numSimulations);
    simulation = new Simulation[numSimulations];
    for (int i=0;i<numSimulations;i++) {
      simulation[i] = new Simulation(simulationXML.getChild(i),numSimulations);
    }
    screenW = screen.width - 220;
  }
  
  void display () {
    for (int i=0;i<numSimulations;i++) {
      simulation[i].display(int(i*screenW/numSimulations+220+i*5));
    }
  }
}


