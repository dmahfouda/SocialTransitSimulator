PFont       font;
PImage      mapImage;
boolean     repopulated;
boolean     stopShort;

void setSimulation() {
  View = 0;
  setScreen();
  setFont();

  maxFleetSize = 0;
  maxVehicleCapacity = 0;
  mutantPercent = 0;
  numGen = 0;
  populationSize = 0;

  beacon = new ArrayList();

  simulationDefaults();
  switchDefaults();
  
  repopulated = true;
  
  //noLoop();
}

void setScreen() {
  size(screen.width-20,screen.height-50);
  background(255);
  fill(0);
}

void setFont () {   
  font = loadFont("AlBayan-10.vlw"); 
  textFont(font, 10);
}

void simulationDefaults () {
  mapSetting = "map.xml";
  populationSetting = "pop.xml";
  testString = "";
  testArray = new int[0];
  maxFleetSizeArray = new int[0];
  maxVehicleCapacityArray = new int[0];
  mutantPercentageArray = new int[0];
  generationsArray = new int[0];
  populationSizeArray = new int[0];
  stopShort = true;
}

void switchDefaults( ) {
//  mapImageSwitch = false;
}


void firstTime () {
  if (first) {
    seed_simulation = new processing.xml.XMLElement(this, "seed_simulation_1.xml");

    gen = 0;

    bestArray = new float[numGen];
    worstArray = new float[numGen];
    averageArray = new float[numGen];
    gafleet = new gaPopulation[numGen];
    //Population(number,traits,trait_resolution)
    gafleet[gen] = new gaPopulation(populationSize,maxFleetSize,2,gen);
    first = false;
  }
}

