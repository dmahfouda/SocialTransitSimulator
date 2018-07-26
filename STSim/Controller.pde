String mapDataFile, mapImageFile, populationDataFile, fleetDataFile, simulationDataFile;
processing.xml.XMLElement mapDataXML, populationDataXML, fleetDataXML, simulationDataXML;
boolean defaultsSet;

void mouseClicked() {
  flipSwitches();
}

void flipSwitches ( ) {
  if (backButton.within) {
    backButton.sWitch = true;
    View = 0;
    backButton.within = false;
  }
  
  if (simulationButton.within) {
    simulationButton.sWitch = true;
    derivationButton.sWitch = false;
    displaySimsButton.sWitch = false;
    View = 1;
    simulationButton.within = false;
  }
  
  if (derivationButton.within) {
    derivationButton.sWitch = true;
    simulationButton.sWitch = false;
    displaySimsButton.sWitch = false;
    View = 2;        
    derivationButton.within = false;
  }
  
  if (displaySimsButton.within) {
    displaySimsButton.sWitch = true;
    simulationButton.sWitch = false;
    derivationButton.sWitch = false;
    View = 3;
    displaySimsButton.within = false;
  }
  
  if (chooseSimsButton.within) {
    String simulationDataFile = selectInput();
    if (simulationDataFile != null) {   
      simulationDataXML = new processing.xml.XMLElement(this, simulationDataFile);
      sims = new Simulations(simulationDataXML);
      chooseSimsButton.sWitch = true;
    }
    return;
  } 
    
  if (setDefaultsButton.within) {
    mapDataFile = "map1.xml";
    if (mapDataFile != null) {
      mapDataXML = new processing.xml.XMLElement(this, mapDataFile);
      mnbprtodi = new MNBPRTODI(mapDataXML);
      mnbprtodi.connectStations();
      mapDataButton.sWitch = true;
    }
    
    mapImageFile = "map1.png";
    if (mapImageFile != null) {    
      mapImage = loadImage(mapImageFile);
      mapImageButton.sWitch = true;
    }
    
    populationDataFile = "population1.xml";
    if (populationDataFile != null) {    
      populationDataXML = new processing.xml.XMLElement(this, populationDataFile);
      population = new Population(populationDataXML);
      population.populate();
      populationDataButton.sWitch = true;
    } 
    
    populationSize = 50;
    maxVehicleCapacity = 10; 
    maxFleetSize = 10;
    mutantPercent = 20;
    numGen = 50;
    
    fleetSizeButton.addName = "10";
    vehicleCapacityButton.addName = "10";
    mutantPercentageButton.addName = "20";
    generationsButton.addName = "50";
    populationSizeButton.addName ="50";
    
    if (simulationButton.sWitch) {
      fleetDataFile = "fleet1.xml";
      if (fleetDataFile != null){
        fleetDataXML = new processing.xml.XMLElement(this,fleetDataFile);
        fleet = new Fleet(fleetDataXML);
        fleet.build_cars1();
      }
    }
    
    defaultsSet = true;
    return;
    
  }  
  
  if (mapDataButton.within) {
    String mapDataFile = selectInput();
    if (mapDataFile != null) {
//      processing.xml.XMLElement mapDataXML;
      mapDataXML = new processing.xml.XMLElement(this, mapDataFile);
      mnbprtodi = new MNBPRTODI(mapDataXML);
      mnbprtodi.connectStations();
      mapDataButton.sWitch = true;
    }
    return;
  }  

  if (mapImageButton.within){
    String mapImageFile = selectInput();
    if (mapImageFile != null) {    
      mapImage = loadImage(mapImageFile);
      mapImageButton.sWitch = true;
    }
    return;
  }  
  
  if (populationDataButton.within){
    String populationDataFile = selectInput();
    if (populationDataFile != null) {    
//      processing.xml.XMLElement populationDataXML;
      populationDataXML = new processing.xml.XMLElement(this, populationDataFile);
      population = new Population(populationDataXML);
      population.populate();
      populationDataButton.sWitch = true;
    }
    return;
  }  
  
  if (fleetDataButton.within){
    String fleetDataFile = selectInput();
    if (fleetDataFile != null) {    
//      processing.xml.XMLElement fleetDataXML;
      fleetDataXML = new processing.xml.XMLElement(this, fleetDataFile);
      fleet = new Fleet(fleetDataXML);
      fleet.build_cars1();
      fleetDataButton.sWitch = true;
    }
    return;
  }  
  
  if (fleetSizeButton.within){
    fleetSizeButton.sWitch = true;
    vehicleCapacityButton.sWitch = false;
    mutantPercentageButton.sWitch = false;
    generationsButton.sWitch = false;
    populationSizeButton.sWitch = false;
    runButton.sWitch = false;
    return;
  }  

    if (vehicleCapacityButton.within){
      fleetSizeButton.sWitch = false;
      vehicleCapacityButton.sWitch = true;
      mutantPercentageButton.sWitch = false;
      generationsButton.sWitch = false;
      populationSizeButton.sWitch = false;
      runButton.sWitch = false;
      return;
    }  
  
   if (mutantPercentageButton.within){
      fleetSizeButton.sWitch = false;
      vehicleCapacityButton.sWitch = false;
      mutantPercentageButton.sWitch = true;
      generationsButton.sWitch = false;
      populationSizeButton.sWitch = false;
      runButton.sWitch = false;
      return;
   }  
  
  if (generationsButton.within){
    fleetSizeButton.sWitch = false;
    vehicleCapacityButton.sWitch = false;
    mutantPercentageButton.sWitch = false;
    generationsButton.sWitch = true;
    populationSizeButton.sWitch = false;
    runButton.sWitch = false;
    return;
  }  
  
  if (populationSizeButton.within){
    fleetSizeButton.sWitch = false;
    vehicleCapacityButton.sWitch = false;
    mutantPercentageButton.sWitch = false;
    generationsButton.sWitch = false;
    populationSizeButton.sWitch = true;
    runButton.sWitch = false;
    defaultsSet = true;
    return;
  }
  
  if (runButton.within){
    fleetSizeButton.sWitch = false;
    vehicleCapacityButton.sWitch = false;
    mutantPercentageButton.sWitch = false;
    generationsButton.sWitch = false;
    populationSizeButton.sWitch = false;
    runButton.sWitch = true;
    
    mapImageButton.sWitch = false;
    mapDataButton.sWitch = false;
    populationDataButton.sWitch = false;
    defaultsSet = true;
    windowController();
    return; 
  }
  
  if (pauseButton.within){
    //if pause is on
    if (pauseButton.sWitch) {
      pauseButton.sWitch = false;
      //turn pause off
      println("pauseButton: loop");
      loop();
      return;
    } else {
      //if pause is off
      //turn pause on
      pauseButton.sWitch = true;
      println("pauseButton: noLoop");
      noLoop();
      return;
    }
  }
  
  if (resetButton.within) {
    fleetSizeButton.sWitch = false;
    vehicleCapacityButton.sWitch = false;
    mutantPercentageButton.sWitch = false;
    generationsButton.sWitch = false;
    populationSizeButton.sWitch = false;
    runButton.sWitch = false;
    
    mapImageButton.sWitch = false;
    mapDataButton.sWitch = false;
    populationDataButton.sWitch = false;
//    defaultsSet = false;
    displaySims = false;
    chooseSimsButton.sWitch = false;
    println ("reset");
    return;
  }
  
  if (exitButton.within) {
    exit();
  }
}









