import processing.core.*; 
import processing.xml.*; 

import proxml.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class STSim extends PApplet {

//I'm just changing this so that git has something to track


processing.xml.XMLElement seed_simulation;

XMLInOut xmlInOut;
proxml.XMLElement xml_simulations;

MNBPRTODI   mnbprtodi;
Fleet       fleet;
Population  population;
Simulations sims;

ArrayList   beacon;
long[]      time;
int         arrived_list,iterat,iterations;

int maxFleetSize, maxVehicleCapacity, populationSize;
float mutantPercent;

float[]     total_waste;
long[]      total_travel_time;

gaPopulation[] gafleet;
int numGen;
int gen;    //this is a counter

float[] worstArray,bestArray,averageArray;

int View;

String populationSetting,mapSetting,testString;
boolean first = true;
boolean displaySims;

/*******************************************************************************************************/
public void setup () {
  setSimulation();
  controller();
  
  displaySims = false;
  
  iterat = 0;
  iterations = 2;
  
   time = new long[iterations];
  for (int i=0;i<iterations;i++){
    time[i]=0;
  }
  
   total_waste = new float[iterations];
  for (int i=0;i<iterations;i++){
    total_waste[i]=0;
  }

  total_travel_time = new long[iterations];
  for (int i=0;i<iterations;i++){
    total_travel_time[i]=0;
  }
  
   //load simulations from file if it exists
  xmlInOut = new XMLInOut(this);
  try{
    xmlInOut.loadElement("simul.xml"); 
  }
  catch(Exception e){
    //if the xml file could not be loaded it has to be created
    xmlEvent(new proxml.XMLElement("simulations"));
  }
  
}

public void xmlEvent(proxml.XMLElement element){
  xml_simulations = element;
  xmlInOut.saveElement(xml_simulations,"simulations.xml"); 
}


/*********************************************************************************************/
public void draw () {
  background(255);
  
  //setup
  controller();
  windowController();
  println("setup"); 
  
  //derivation
  if(runButton.sWitch && derivationButton.sWitch) {
    println("derivation");
    controller();
    windowController();
    firstTime();
    if (gen<numGen-1) {
      gafleet[gen].regenerate();
      gafleet[gen-1].getTrends();
      for (int i=0;i<=gen-1;i++) {
        gafleet[i].displayBest();
      }
      displayTrends();
    } 
    else {
      save("ga_4_20a.tiff");  
      exit();
    }
    println("generation: "+gen);
  }
  
  //simulation
  if (runButton.sWitch && simulationButton.sWitch) {
   println("simulation"); 
   if (mapImage!=null) { 
     image(mapImage,201,0,width-201,height);
   } else {
     noStroke();
     fill(255);
     rect(201,0,width-201,height);
   }
   
   noStroke();
   
   if(repopulated) {
      for (int i=0;i<population.populationxml.getChildCount();i++) {
        population.person[i].queue();                      
      }
      delay(50);//to load xml file
      repopulated = false;
    }

    for(int i=0;i<mnbprtodi.numStations;i++) {
        mnbprtodi.station[i].display();
    }
    
  //car methods are placed in between person methods so poeple can be visible over cars
    for (int i=0;i<fleet.fleetxml.getChildCount();i++){
      fleet.car[i].navigate();
      fleet.car[i].display();
    }

    if (arrived_list == population.person.length) { 
      iterat++;
      repopulated = true;
      arrived_list = 0;

//      xml constructor here to load efficiencies
      populate_xml();
      population.repopulate();
      
      //rebuild fleet
      fleetDataFile = "fleet1.xml";
      if (fleetDataFile != null){
        fleetDataXML = new processing.xml.XMLElement(this,fleetDataFile);
        fleet = new Fleet(fleetDataXML);
        fleet.build_cars1();
      }
      stopShort = false;
      
      if (iterat == iterations){  
        String simulationsName = "simulationResults/simulations"+month()+day()+year()+hour()+minute()+second()+".xml";
        xmlInOut.saveElement(xml_simulations, simulationsName);
        runButton.sWitch = false;
        simulationButton.sWitch = false;
        //reload xml file
        processing.xml.XMLElement simsXML = new processing.xml.XMLElement(this, simulationsName);
        sims = new Simulations(simsXML);
        chooseSimsButton.sWitch = true;
      }
    }

    display_waste();

    if (iterat < iterations){
      time[iterat]++;
    }
  }  
}

/*****************************interrupts********************************/
//void keyPressed () {
//  if (key == 'p') {
//    noLoop();
//  }
//  else {
//    loop();
//  }
//}

public void displayTrends ( ) {
  strokeWeight(2);
  stroke(0,255,0);
  for (int i=0;i<gen-1;i++){
    //display best
    stroke(0,255,0);
    line(i*10,height-map(bestArray[i],-10000,10000,0,height),i*10+10,height-map(bestArray[i+1],-10000,10000,0,height));

    //display worst
    stroke(255,0,0);
    line(i*10,height-map(worstArray[i],-10000,10000,0,height),i*10+10,height-map(worstArray[i+1],-10000,10000,0,height));

    //display average
    stroke(0,0,255);
    line(i*10,height-map(averageArray[i],-10000,10000,0,height),i*10+10,height-map(averageArray[i+1],-10000,10000,0,height));
  }
}

public void displayInitialBest ( ) {
  stroke(0);
  line(500,0,500,400);
  translate(500,0);
  gafleet[0].best.display();
}

/****************************************************/
public void display_waste(){
  int gap = floor(height/iterations);

  for (int i=0;i<iterations;i++){
    fill(255,0,0,75);
    rectMode(CORNER);
    rect(0,gap*i,map(total_waste[i],0,100000,0,width),gap/2);
    fill(0,255,0,75);
    rect(0,gap*i+gap/2,map(total_travel_time[i],0,50000,0,width),gap/2);
  }
}





String mapDataFile, mapImageFile, populationDataFile, fleetDataFile, simulationDataFile;
processing.xml.XMLElement mapDataXML, populationDataXML, fleetDataXML, simulationDataXML;
boolean defaultsSet;

public void mouseClicked() {
  flipSwitches();
}

public void flipSwitches ( ) {
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









//  if (simulationSettings.isWithin() && View == 0){
//    View = 1;
//  } 
//  else {
//    if (simulationSettings.isWithin() && View == 1){
//      View = 0;
//    } 
//    else {
//      if (View == 1 && mapSettings.isWithin()) {
//        String mapSetting = loadFile();
//      }
//      else {
//        if (View == 1 && populationSettings.isWithin()) {
//          String populationSetting = loadFile();
//        }
//      }
//    }
//  }

//
//void view1 () {
//  derivationSettings.update();
//  simulationSettings.update();
//  mapSettings.update();
//  populationSettings.update();
//  run.update();
//  populationSettings.display(30,80);
//  mapSettings.display(30,50);
//  simulationSettings.display(10,20);
//  derivationSettings.display(10,110);
//  run.display(10,140);
//  loop();
//}

//String loadFile() {
//  String loadPath = selectInput();  // Opens file chooser
//  if (loadPath == null) {
//    // If a file was not selected
//    println("No file was selected...");
//    return loadPath;
//  } 
//  else {
//    // If a file was selected, print path to file
//    println(loadPath);
//    return loadPath;
//  }
//}

////HERE -- NEED TO MODIFY THE EVALUATE BUTTON
//void evaluate ( ) {
//  if (mapOptions) {
//    View = 1;
//    return;
//  } else {
//    View = 0;
//  }
//  if (populationOptions) {
//     View =  2;
//     return;
//  } else {
//    View = 0;
//  }
//  if (fleetOptions) {
//    View = 3;
//    return;
//  } else {
//    View = 0;
//  } 
//}
//

//    //cull stations    
//    Station[] station2 = new Station[0];
//    //remove nodes that are not in ways;
//    for (int i=0;i<station.length;i++){
//      boolean inWay = false;
//      for (int j=0;j<way.length;j++) {
//        for (int k=0;k<way[j].stationName.length;k++){
//          if (station[i].name == way[j].stationName[k]) {
//            inWay = true;
//          }
//        }
//      }
//      if (inWay) {
//        station2 = (Station[])append(station2, station[i]);
//        numStations++;
//      }
//    }
//    station = station2;
class Simulation {
  float fitness;
  float cost;
  int otherChildren;
  
  Simulation (processing.xml.XMLElement sim, int oC) {
    otherChildren = oC;
    processing.xml.XMLElement efficiencyXML = sim.getChild(3);
    processing.xml.XMLElement fitnessXML = efficiencyXML.getChild(0);
    fitness = fitnessXML.getFloatAttribute("fitness");
    cost = fitnessXML.getFloatAttribute("cost");
  }

  public void display() {
    fill(0,0,255,50);
    int scrn = screen.height-50;
    float y = map(cost,0,10000,0,scrn);
    rect(0,scrn - y,(screen.width-260)/otherChildren, y);
  }
  
  public void display(int x) {
    fill(0,0,255,50);
    noStroke();
    int scrn = screen.height - 50;
    float y = map(cost,0,10000,0,scrn);
    rect(x,scrn - y,(screen.width-300)/otherChildren, y);
    stroke(255);
    text("cost = total_travel_time+total_waste+total_vehicle_cost = "+cost, x, scrn-(y+5));
  }

}
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
  
  public void display () {
    for (int i=0;i<numSimulations;i++) {
      simulation[i].display(PApplet.parseInt(i*screenW/numSimulations+220+i*5));
    }
  }
}


public void controller () {
  switch(View) {
  case 0: 
    view0();
    break;
  case 1:
    view1();
    break;
  case 2:
    view2();
    break;
  case 3:
    view3();
    break;
  default:             
    println("None");   
    break;
  }
}

public void view0 () {
  stroke(0);
  strokeWeight(1);
  line(200,0,200,screen.height);
  line(0,330,200,330);
  line(0,screen.height-110,200,screen.height-110);
  
  simulationButton.update();
  derivationButton.update();
  displaySimsButton.update();
  exitButton.update();
  
  simulationButton.display(20,30);
  derivationButton.display(20,60);
  displaySimsButton.display(20,90);
  exitButton.display(20,screen.height-80);
} 

public void view1 () {
  stroke(0);
  strokeWeight(1);
  line(200,0,200,screen.height);
  line (0,330,200,330);
  line (0,screen.height-170,200,screen.height-170);
  
  backButton.update();
  mapDataButton.update();
  mapImageButton.update();
  
  populationDataButton.update();
  
  fleetDataButton.update();
  
  setDefaultsButton.update();
  runButton.update();
  pauseButton.update();
  resetButton.update();
  exitButton.update();

  backButton.display(20,30);
  mapDataButton.display(20,60);
  mapImageButton.display(20,90);
  populationDataButton.display(20,120);
  fleetDataButton.display(20,150);
  setDefaultsButton.display(20,180);
  runButton.display(20,210);
  pauseButton.display(20,screen.height-140);
  resetButton.display(20,screen.height-110);
  exitButton.display(20,screen.height-80);
}

public void view2 () {
  stroke(0);
  strokeWeight(1);
  line (200,0,200,screen.height);
  line (0,360,200,360);
  line (0,screen.height-170,200,screen.height-170);
  
  backButton.update();
  mapDataButton.update();
  mapImageButton.update();
  populationDataButton.update();
  fleetSizeButton.update();
  vehicleCapacityButton.update();
  mutantPercentageButton.update();
  generationsButton.update();
  populationSizeButton.update();
  runButton.update();
  setDefaultsButton.update();
  pauseButton.update();
  resetButton.update();
  exitButton.update();
  
  backButton.display(20,30);
  mapDataButton.display(20,60);
  mapImageButton.display(20,90);
  populationDataButton.display(20,120);
  fleetSizeButton.display(20,150);
  vehicleCapacityButton.display(20,180);
  mutantPercentageButton.display(20,210);
  generationsButton.display(20,240);
  populationSizeButton.display(20,270);
  runButton.display(20,300);
  setDefaultsButton.display(20,330);
  
  pauseButton.display(20,screen.height-140);
  resetButton.display(20,screen.height-110);
  exitButton.display(20,screen.height-80);
}

public void view3 () {
  stroke(0);
  strokeWeight(1);
  line(200,0,200,screen.height);
  line(0,330,200,330);
  line(0,screen.height-140,200,screen.height-140);
  
  backButton.update();
  chooseSimsButton.update();
  exitButton.update();
  resetButton.update();

  backButton.display(20,30);
  chooseSimsButton.display(20,60);
  
  resetButton.display(20,screen.height-110);
  exitButton.display(20,screen.height-80);
} 


class Way {
  int[] stationName;
  String name;

  Way (processing.xml.XMLElement wy) {
    stationName = new int[0];
    for (int i=0;i<wy.getChildCount();i++) {
      processing.xml.XMLElement xmlChild = wy.getChild(i);
      String isName = xmlChild.getName();
      String ndName = "nd";
      String tagName = "tag";
      //if element is a node
      if (isName.equals(ndName)) {
        stationName = append(stationName, xmlChild.getIntAttribute("ref"));
      }
    }
  }

  //  void display () {
  //    stroke(0);
  //    strokeWeight(1);
  //
  //    for (int i=1;i<wNode.length;i++) {
  //      line(wNode[i-1].x, wNode[i-1].y,wNode[i].x,wNode[i].y);
  //    }
  //  }

  public void wConnect(){
    Station one = null;
    Station two = null;
    for (int i=1;i<stationName.length;i++) {
      for (int j=0;j<mnbprtodi.numStations;j++) {
        if(mnbprtodi.station[j].name == stationName[i-1]) {
          one = mnbprtodi.station[j];
        }
        if(mnbprtodi.station[j].name == stationName[i]) {
          two = mnbprtodi.station[j];
        }
      }
      if (one != null && two != null) {
        one.connected = true;
        two.connected = true;
        connect_both(one,two);
      }
    }  
  }

}

public void populate_xml () {//simulation
  proxml.XMLElement xml_simulation = new proxml.XMLElement("simulation");
  xml_simulations.addChild(xml_simulation);  

  //population    
  proxml.XMLElement xml_population = new proxml.XMLElement("population");
  xml_simulation.addChild(xml_population);

  for (int i=0;i<population.populationxml.getChildCount();i++){      
    proxml.XMLElement xml_person = new proxml.XMLElement("person");
    xml_person.addAttribute("name",population.person[i].name);
    xml_population.addChild(xml_person);  

    proxml.XMLElement xml_itinerary = new proxml.XMLElement("itinerary");
    xml_itinerary.addAttribute("start",population.person[i].start.name);
    xml_itinerary.addAttribute("destination",population.person[i].destination.name);
    xml_person.addChild(xml_itinerary);

    proxml.XMLElement xml_transport_info = new proxml.XMLElement("transport_info");
    xml_person.addChild(xml_transport_info);

    proxml.XMLElement xml_transport_time = new proxml.XMLElement("transport_time");
    xml_transport_info.addChild(xml_transport_time);

    proxml.XMLElement xml_wait_time = new proxml.XMLElement("wait_time");
    xml_wait_time.addAttribute("time_finish", population.person[i].wait_end);
    xml_wait_time.addAttribute("time_start", population.person[i].wait_start);
    xml_transport_time.addChild(xml_wait_time);

    proxml.XMLElement xml_travel_time = new proxml.XMLElement ("travel_time");
    xml_travel_time.addAttribute("time_finish", population.person[i].travel_end);
    xml_travel_time.addAttribute("time_start", population.person[i].travel_start);
    xml_transport_time.addChild(xml_travel_time);

    proxml.XMLElement xml_travel_distance = new proxml.XMLElement("travel_distance");
    xml_travel_distance.addAttribute("distance", population.person[i].travel_distance);
    xml_transport_info.addChild(xml_travel_distance);
  }

  //mmnbprtodi
  proxml.XMLElement xml_mnbprtodi = new proxml.XMLElement("mnbprtodi");
  xml_simulation.addChild(xml_mnbprtodi);

  //station
  for (int i=0;i<mnbprtodi.station.length;i++) {
    proxml.XMLElement xml_station = new proxml.XMLElement("station");

    xml_station.addAttribute("name", mnbprtodi.station[i].name);
    xml_station.addAttribute("x", mnbprtodi.station[i].x);
    xml_station.addAttribute("y", mnbprtodi.station[i].y);
    for (int j=0;j<mnbprtodi.station[i].linked_to_station.length;j++) {
      xml_station.addAttribute("linked_to_station"+j,mnbprtodi.station[i].linked_to_station[j].name);
    }
    xml_mnbprtodi.addChild(xml_station);
  }  

  //fleet
  proxml.XMLElement xml_fleet = new proxml.XMLElement("fleet");
  xml_simulation.addChild(xml_fleet);

  for(int i=0;i<fleet.car.length;i++){
    proxml.XMLElement xml_car = new proxml.XMLElement("car");

    xml_car.addAttribute("name",fleet.car[i].name);
    //THIS FIELD IS WEIRD _ WE ARE LOOKING FOR THE START CONFIGURATION
    //FOR CARS, BUT HAVEN'T FOUND IT YET
    xml_car.addAttribute("location",fleet.car[i].next.name);//car starts at previous destination -- this isn't always true
    xml_car.addAttribute("capacity",fleet.car[i].capacity); 
    xml_car.addAttribute("speed",fleet.car[i].speed);
    xml_car.addAttribute("travel_distance",fleet.car[i].travel_distance);
    //car.addAttribute("beahavior",behavior);

    //car.addAttribute("efficiency",efficiency);
    //car.addAttribute("operating_time",operating_time);

    xml_fleet.addChild(xml_car);
  }

  //efficiencies
  proxml.XMLElement xml_efficiency = new proxml.XMLElement("efficiency");
  xml_simulation.addChild(xml_efficiency);

  proxml.XMLElement xml_fitness = new proxml.XMLElement("fitness");

  float total_vehicle_cost = 0;
  float total_travel_time = 0;
  float total_waste = 0;
  float fitness_constant = 10000;

  for (int i=0;i<population.person.length;i++) {
    total_travel_time += population.person[i].travel_end - population.person[i].wait_start; 
  }

  for (int i=0;i<fleet.car.length;i++) {
    total_waste += fleet.car[i].waste; 
    total_vehicle_cost += fleet.car[i].capacity*100;
  }
  
  float cost = total_travel_time+total_waste+total_vehicle_cost;

  float fitness = floor(fitness_constant - (total_travel_time+total_waste+total_vehicle_cost));
  
  xml_fitness.addAttribute("totalTravelTime", total_travel_time);
  xml_fitness.addAttribute("totalVehicleCost", total_vehicle_cost);
  xml_fitness.addAttribute("fitness",fitness);
  xml_fitness.addAttribute("cost",cost);

  xml_efficiency.addChild(xml_fitness);
}

class Button {
  int x;
  int y;
  int bheight = -20;
  int bwidth = 160;
  String name;
  int fillColor;
  String addName;
  boolean sWitch;
  boolean within;
  int[] numArray;

  Button (int xx, int yy, String n) {
    x = xx;
    y = yy;
    name = n;
    fillColor=255;
    addName = "";
    sWitch = false;
    within = false;
    numArray = new int[0];
  }

  Button (String n) {
    x = 0;
    y = 0;
    name = n;
    fillColor=255;
    addName = "";
    sWitch = false;
    numArray = new int[0];
  }

  public void display () {
    fill(fillColor);
    stroke(0);
    rect(x-5,y+5,bwidth,bheight);  
    fill(0);
    text(name+addName, x,y);
  }

  public void display (int xx, int yy) {
    x = xx;
    y = yy;
    fill(fillColor);
    stroke(0);
    rectMode(CORNER);
    rect(x-5,y+5,bwidth,bheight);  
    fill(0);    
    text(name+addName, x,y);
  }

  public void update () {
    isWithin();
    if (within) {
      fillColor = 200;
    } 
    else {
      fillColor = 255;
    }
  }

  public void isWithin() {
    if (x-5 < mouseX && mouseX < x+bwidth && y+bheight < mouseY && mouseY < y+5) {
      within = true;
    } 
    else {
      within = false;
    } 
  }

  public void updateName () {
    if (sWitch) {
      if (isNumeral(key)){
        int y = convertKey(key);
        numArray = append(numArray,y);
      }
      if (key == DELETE || key == BACKSPACE) {
        if (numArray.length>0){
          numArray = shorten(numArray);
        }
      }

      String tempString = "";

      for (int i=0;i<numArray.length;i++){
        tempString += numArray[i];
      }

      addName = tempString;
    } 
  }
} 


//0 order buttons
Button backButton = new Button ("<");

//first order buttons
Button simulationButton = new Button ("simulation");
Button derivationButton = new Button ("derivation");
Button displaySimsButton = new Button ("displaySimulations");

// second order buttons
Button mapDataButton = new Button ("mapData");
Button mapImageButton = new Button ("mapImage"); 
Button populationDataButton = new Button ("populationData");

// buttons unique to derivation
Button fleetSizeButton = new Button("maxFleetSize:");
Button vehicleCapacityButton = new Button("maxVehicleCapacity:");
Button mutantPercentageButton = new Button ("mutantPercentage:");
Button generationsButton = new Button ("numberOfGenerations:");
Button populationSizeButton = new Button ("populationSize:");
//convergence setting

//buttons unique to simulation
Button fleetDataButton = new Button ("fleetData");

//buttons unique to displaySims
Button chooseSimsButton = new Button ("chooseSimuilations");

//third order buttons
Button runButton = new Button("run");
Button setDefaultsButton = new Button("setDefaults");
Button pauseButton = new Button("pause");
Button resetButton = new Button("reset");
Button exitButton = new Button("exit");


class Organism {
  int num_traits;
  int trait_res;
  Trait[] trait;
  int fitness;
//  float mutantPercent = .2;
  Organism mom,dad;
  //spec traits
  //array to store mapped trait values
  int[] mappedTrait; 
  Simulator simulation;

  Organism (int num, int res) {
    num_traits = num;
    trait_res = res;    
    trait = new Trait[num_traits];
    mappedTrait = new int[num_traits];
    make();
  }

  Organism (Organism rmom, Organism rdad) {
    mom = rmom;
    dad = rdad;
    num_traits = mom.num_traits; 
    trait_res = mom.trait_res;    
    trait = new Trait[num_traits];
    mappedTrait = new int[num_traits];
    make();
    breed();
  }

  public void make ( ) {
    for (int i=0;i<num_traits;i++) {
      trait[i] = new Trait(trait_res);
    }
  }

  public void breed ( ) {
    //chose random crossover
    int crossover = floor(random(0,trait_res));

    for (int j=0;j<num_traits;j++) {
      for (int i=0;i<trait_res;i++) {
        if (i<crossover) {
          trait[j].gene[i] = mom.trait[j].gene[i];
        } 
        else {
          trait[j].gene[i] = dad.trait[j].gene[i];
        }
      }
    }

    mutate();
  }

  public void mutate ( ) {
    float mTest = random(0,1);
    if (mTest<mutantPercent) {
      //mutate random gene
      trait[floor(random(0,num_traits))].gene[floor(random(0,trait_res))] = floor(random(0,10));
    }  
  }  

  public void getFit ( ) {
    int fitness_factor = 1000;
    float lower_bound = 0;
//    println("maxVehicleCapacity: "+maxVehicleCapacity);
    float upper_bound = maxVehicleCapacity;
//    println ("here b");
    //(express each trait as num between lower and upper bound
    for (int i=0;i<num_traits;i++) {
      mappedTrait[i] = floor(map(trait[i].intValue(),0,pow(10,trait_res),lower_bound,upper_bound));
    }
//    println ("here c");
    //find fitness
    simulation = new Simulator(mappedTrait);
//    println ("here d");
    fitness = simulation.run();
//    println ("here e");  
  }

  public void display () {
    for (int i=0;i<mappedTrait.length;i++) {
      int xx = 20; 
      //modulo should be a variable
      fill(0);
      if (i>4) {xx = 50;}
      rect(xx,(i%5)*10+20,mappedTrait[i],5);
      text(mappedTrait[i],xx+mappedTrait[i]+3,(i%5)*10+27);
    }
  }  
}


class gaPopulation {
  int num_members;
  int num_traits;
  int trait_res;
  int x_trans,y_trans;
  int myGen;

  Organism best, worst; 
  float average; //average is not an organism

  Organism[] member;
  Organism[] parent;
  Organism[] child;

  gaPopulation (int num, int n_t, int t_r,int mG) {
    num_members = num;
    num_traits = n_t;
    trait_res = t_r;
    myGen = mG;

    member = new Organism[num_members];
    parent = new Organism[num_members];
    child = new Organism[num_members];

    x_trans = 0;
    y_trans = 0;

    make();
  }

  public void make () {
    for (int i=0;i<num_members;i++) {
      member[i] = new Organism(num_traits, trait_res);
    }
  }

  public void getFit() {
    for (int j=0;j<num_members;j++){
//      println("here a");
      member[j].getFit();
      println("getFit()");
    }
  }

  // gen is the number of generations
  public void regenerate ( ) {
    //1.evaluate fitness of each member
    for (int j=0;j<num_members;j++){
//    controller();
      member[j].getFit();
      println("simulation no: "+j+" fitness = "+member[j].fitness);
    }

    //2.select parents - tournament method
    selectParents();

    //3.breed children -- mutation happens during breeding
    breedChildren();

    //4.set parents = children;
    gen++;
//    println("gen: "+gen);
    gafleet[gen] = new gaPopulation (num_members, num_traits, trait_res,gen);
//    println("gafleet[gen].myGen: "+gafleet[gen].myGen);
    gafleet[gen].member = child; 
//    println("gafleet[gen].myGen: "+gafleet[gen].myGen);
  }

  public void selectParents () {
    for (int j=0;j<num_members;j++) {
      int rand1 = floor(random(0,num_members));
      int rand2 = floor(random(0,num_members));

      //evaluate fitness of two randomly selected memberss
      if (member[rand1].fitness > member[rand2].fitness) {
        parent[j] = member[rand1];
      } 
      else {
        parent[j] = member[rand2];
      }
    }
  }

  public void breedChildren() {
    Organism randMom, randDad;
    int crossover;

    for (int i=0;i<num_members;i++){
      //randomly select two parents
      randMom = parent[floor(random(0,populationSize))];
      randDad = parent[floor(random(0,populationSize))];

      child[i] = new Organism (randMom, randDad);
    }
  }

  public void getBest ( ) {
    int j = 0;
    for (int i=0;i<num_members;i++){
      if (member[j].fitness < member[i].fitness) { 
        j=i;
      }
    }
    best = member[j];
  }

  public void getAverage ( ) {
    for (int i=0;i<num_members;i++){
      average += member[i].fitness;
    }

    average = average/num_members;
  }

  public void getWorst ( ) {
    int j = 0;
    for (int i=0;i<num_members;i++){
      if (member[j].fitness > member[i].fitness) { 
        j=i;
      }
    }
    worst = member[j];  
  }

  public void getTrends ( ) {
    getBest();
    getWorst();
    getAverage();

    println("best fitness = "+ best.fitness);

    bestArray[gen-1] = best.fitness;
    worstArray[gen-1] = worst.fitness;
    averageArray[gen-1] = average;
  }

  public void displayBest ( ) {
    int thisGen = gen-1;
    x_trans = myGen%10;
    if (myGen > 9) {
      y_trans = 100;
    }
    if (myGen > 19) {
      y_trans = 200;
    }
    if (myGen > 29) {
      y_trans = 300;
    }
    if (myGen > 39) {
      y_trans = 400;
    }
    pushMatrix();
    stroke(0);
    strokeWeight(0);
    translate(200+x_trans*100,y_trans);
    best.display();
    text("gen:"+myGen+" fit:"+best.fitness,20,90);
    popMatrix();
  }
}    



class Trait {
  int trait_res;
  int[] gene;

  Trait(int t_r) {
    trait_res = t_r;
    gene = new int[trait_res];
    make();
  }

  public void make () {
    for (int i=0;i<trait_res;i++) {
      gene[i] = floor(random(0,10));
    }
  }

  public int intValue ( ) {
    int power = trait_res;
    int intval = 0;
    for (int i=0;i<trait_res;i++) {
      intval += gene[i]*10^power--;
    }
    return intval;
  }
}


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
  public int run () {
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
  public void setInitials () {
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
  public void simulation_constructor() {
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
  public void navigate () {
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
  public void stop () {
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
  public boolean passenger_wants_out () {
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
  public void follow_road() {
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
  public void carry_cargo(){
    for (int i=0;i<cargo.size();i++){
      Person p = (Person) cargo.get(i);
      p.in_transit();
    }
  }


  /********************************************************************************************************
   * LISTEN_TO_PASSENGERS_FUNCTION
   *********************************************************************************************************/
  public void listen_to_passengers(){
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
  public void turn() {
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
  public void display () {
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
  public void read_beacon() {
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
  public void cargo_display () {
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
  public Station cb3 () {
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
  public Station cb4 () {
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
  public void stop_short() {
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



//*********************************************************************************************************

//this function connects station a to station b,
//calculates the distance between the two stations
//and stores that distance in the "distance_to_station" field
public void connect (Station a, Station b) {
  a.linked_to_station = (Station[]) append(a.linked_to_station, b);
  float distance = dist(a.x,a.y,b.x,b.y);
  a.distance_to_station = append(a.distance_to_station, distance);
}


//this function connects station a to station b,
//calculates the distance between the two stations
//and stores that distance in the "distance_to_station" field
public void connect_both(Station a, Station b) {
  //connect a to b
  a.linked_to_station = (Station[]) append(a.linked_to_station, b);
  float distance = dist(a.x,a.y,b.x,b.y);
  a.distance_to_station = append(a.distance_to_station, distance);

  //then connect b to a
  b.linked_to_station = (Station[]) append(b.linked_to_station, a);
  b.distance_to_station = append(b.distance_to_station, distance);
}



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
  public void build_cars () {
    for (int i=0;i<numCars;i++) {
      // Car (int cap, int star, int spe, int behav, int n)
      //make start argument random   
      car[i] = new Car(mappedTraits[i],90,0,1,i);
    }
  }
  
  /*****************************buildCars1********************************/
  public void build_cars1 () {
    for (int i=0;i<fleetxml.getChildCount();i++) {
      processing.xml.XMLElement carxml = fleetxml.getChild(i);
      car[i] = new Car(carxml.getIntAttribute("capacity"), carxml.getIntAttribute("location"), carxml.getIntAttribute("speed"),1,i);
    }
  }
  
}


class MNBPRTODI {
  processing.xml.XMLElement mnbprtodixml;
  Station[] station;
  Way[] way;
  float minlat,minlon,maxlat,maxlon;
  int childCount;
  int numStations;

  /***************************
   * CONSTRUCTOR
   ***************************/
  MNBPRTODI (processing.xml.XMLElement mnbxml){ 
    mnbprtodixml = mnbxml;
    station = new Station[0];
    numStations = 0;

    way = new Way[0];

    childCount = mnbprtodixml.getChildCount();
    println("mnbprtodichildcount: "+childCount);
    processing.xml.XMLElement mapBounds = mnbprtodixml.getChild(0);
    minlat = mapBounds.getFloatAttribute("minlat");
    minlon = mapBounds.getFloatAttribute("minlon");
    maxlat = mapBounds.getFloatAttribute("maxlat");
    maxlon = mapBounds.getFloatAttribute("maxlon");

//    println(minlon+" "+maxlon+", "+minlat+" "+maxlat);

    for (int i=1;i<childCount;i++){
      processing.xml.XMLElement xmlChild = mnbprtodixml.getChild(i);
      String isName = xmlChild.getName();
      String nodeName = "node";
      String wayName = "way";

      //build stations
      if (isName.equals(nodeName)) {
        float nodeLon = xmlChild.getFloatAttribute("lon");
        float nodeLat = xmlChild.getFloatAttribute("lat");
        if (nodeLon > minlon && nodeLon < maxlon && nodeLat > minlat && nodeLat < maxlat) {
          Station tempStation = new Station(xmlChild.getIntAttribute("id"),map(xmlChild.getFloatAttribute("lon"),minlon,maxlon,201,width), map(xmlChild.getFloatAttribute("lat"),minlat,maxlat,height,0), numStations);
          station = (Station[])append(station, tempStation);
          numStations++;
        }
      }

      //build ways
      if (isName.equals(wayName)) {
        processing.xml.XMLElement wayChild;
        String tagName = "tag";
        String wayChildName = "";
        String highwayName = "highway";
        boolean highwaySwitch = false;
        for (int j=0;j<xmlChild.getChildCount();j++){
          wayChild = xmlChild.getChild(j);
          wayChildName = wayChild.getName();
          //if way child is a tag
          if (wayChildName.equals(tagName)) {
            if(wayChild.getStringAttribute("k").equals(highwayName)) {
              highwaySwitch = true;
            }
          }          
        }

        if (highwaySwitch) {
          Way tempWay = new Way(xmlChild);
          way = (Way[])append(way, tempWay);
        }  
      }  
    }
  }

  //************methods*******************
  public void connectStations () {
    for (int i=0;i<way.length;i++){
      way[i].wConnect();
    }
  } 

//  /***************************
//   * BUILD STATIONS
//   ***************************/
//  void build_stations () {
//    //once to build stations
//    for (int i=0;i<mnbprtodixml.getChildCount();i++) {
//      processing.xml.XMLElement stationxml = mnbprtodixml.getChild(i);
//      station[i] = new Station (i,stationxml.getFloatAttribute("x"),stationxml.getFloatAttribute("y")); 
//    }
//
//    //again to connect them
//    for (int i=0;i<mnbprtodixml.getChildCount();i++) {
//      processing.xml.XMLElement stationxml = mnbprtodixml.getChild(i);
//      for (int j=0;j<4;j++){
//        int constat = stationxml.getIntAttribute("linked_to_station"+j,200);
//        if (constat !=200) {
//          connect(station[i],station[constat]);
//        }
//      }
//    }
//  }
}




class Pathfinder {
  Station[] station;
  Station[] path;
  Station loc;
  Station start;
  Station end;
  Station current;

  Pathfinder(Station[] stat) {
    station = new Station[stat.length];
    //this is a pointer, but it should be a copy
    station = stat;
    path = new Station[0];
  }

  /***********************************************************************************
   * if station is unconnected, findpath will put one station in the pathfinder's
   * path array
   ************************************************************************************/
  public void findpath(Station a, Station b) {
    //**reset path array here
    path = new Station[0];

    for (int i=0;i<station.length;i++) {
      //where is there a MAX VALUE
      station[i].distance_from_start = 10000;
      station[i].visited = false;
      station[i].previous = null;

      //start now points to the local copy
      //of the start station
      if (station[i].name == a.name) {
        start = station[i];
      }
      if (station[i].name == b.name) {
        end = station[i];
      }
    }

    start.distance_from_start = 0;

    int j=0;
    boolean br = true;

    while (find_closest(station)) {
      current.visited = true;
      if (current == end) {
        break;
      }

      //find distances first
      for (int i=0;i<current.linked_to_station.length;i++) {
        //if linked station has not been visited, then mark previous 
        //this is to avoid errors that result from doubly connected links
        if (current.linked_to_station[i].visited == false) {
          //find distance between current station, and station[i]
          float d = dist(current.x,current.y,current.linked_to_station[i].x, current.linked_to_station[i].y);
          //distance between here and linked station
          current.distance_to_station[i] = d;
          //if station has already been visited/measured
          if (current.linked_to_station[i].distance_from_start<10000){
            //if old route is longer than new route
            if (current.linked_to_station[i].distance_from_start > d+current.distance_from_start) { 
              //redefine distance between start and linked station
              current.linked_to_station[i].distance_from_start = d+current.distance_from_start;
              //make this previous station
              current.linked_to_station[i].previous = current;
            } 
            // else leave alone 
          } 
          //if station hasn't been visited/measured
          else {
            current.linked_to_station[i].distance_from_start = d+current.distance_from_start;
            current.linked_to_station[i].previous = current;
          }
        }
      }
    }

    j=0;
    //this function returns a shortest path to station b
    Station en = end;
    while (en != null) {
      path = (Station[]) append (path, en);
      en = en.previous;
    }
    path = (Station []) reverse(path);
  } 

  public void display() {
    stroke(255,0,0);
    strokeWeight(3);
    for (int i=1;i<path.length;i++) {
      line(path[i-1].x,path[i-1].y,path[i].x,path[i].y);
    }
  }

  public boolean find_closest(Station[] s) {
    //set min distanct to infinity
    float min_distance = 10000;  
    Station store_station = null;

    for (int i=0;i<s.length;i++){
      //for unvisited stations
      if(s[i].visited == false) {
        //if station is linked
        if(s[i].distance_from_start < min_distance) {
          min_distance = s[i].distance_from_start;
          store_station = s[i];
        }
      }
    }

    if (min_distance < 10000) {
      current = store_station;
      return true;
    } 
    else {
      return false;
    }
  }
} 





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
  public void queue(){
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
  public void requeue () {
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
  public void board(){
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
  public void in_transit(){
    travel_distance += dist(x,y,car.x,car.y);
    x = car.x;                     
    y = car.y;         
  }

  //check station needs to return a boolean so exit_procedure()
  //can account for cargos ArrayList decreasing by 1 aft
  public boolean check_station(){
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
  public void arrive(){
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
  public void display(){
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
  public void populate () {
    for (int i=0;i<numPeople;i++){ 
      processing.xml.XMLElement personxml = populationxml.getChild(i);
      processing.xml.XMLElement itineraryxml = personxml.getChild(0);
      person[i] = new Person(itineraryxml.getIntAttribute("start"),itineraryxml.getIntAttribute("destination"), i);
    }
  }

  /***************************
   * REPOPULATE
   ***************************/
  public void repopulate() { 
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
  public void passenger_exchange(){
    exit_procedure();
    board_procedure();
  }

  /***************************
   * EXIT_PROCEDURE
   ***************************/
  public void exit_procedure() {
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
  public void board_procedure() {
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
  public void display(){
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
  public void queue_display(){
    for (int i=0;i<queue.length;i++) {
      translate(0,10);
      queue[i].display();
    }
    resetMatrix();
  }

  /***************************
   * ARRIVED DISPLAY
   ***************************/
  public void arrived_display() {
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
  public void link_display() {
    strokeWeight(1);
    stroke(153);
    for(int i=0;i<linked_to_station.length;i++) {
      line(this.x,this.y,linked_to_station[i].x,linked_to_station[i].y);
      float arrowx = this.x - .75f*(this.x-linked_to_station[i].x);
      float arrowy = this.y - .75f*(this.y-linked_to_station[i].y);
      fill(230);
      ellipse(arrowx,arrowy,5,5);
    }
  }
}

public void setMaxCars (int[] mCars) {
  maxFleetSize = 0;
  for (int i=0;i<mCars.length;i++) {
    maxFleetSize += (mCars[i]*pow(10,mCars.length-1-i));
  }
}

public void setMaxCapacity (int[] mCap) {
  maxVehicleCapacity = 0;
  for (int i=0;i<mCap.length;i++) {
    maxVehicleCapacity += (mCap[i]*pow(10,mCap.length-1-i));
  }
}

public int arrayToInt (int[] tempArray) {
  int tempInt = 0;
  for (int i=0;i<tempArray.length;i++) {
    tempInt += (tempArray[i]*pow(10,tempArray.length-1-i));
  }
  return tempInt;
}


PFont       font;
PImage      mapImage;
boolean     repopulated;
boolean     stopShort;

public void setSimulation() {
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

public void setScreen() {
  size(screen.width-20,screen.height-50);
  background(255);
  fill(0);
}

public void setFont () {   
  font = loadFont("AlBayan-10.vlw"); 
  textFont(font, 10);
}

public void simulationDefaults () {
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

public void switchDefaults( ) {
//  mapImageSwitch = false;
}


public void firstTime () {
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

int[]       testArray;
int[]       maxFleetSizeArray;
int[]       maxVehicleCapacityArray;
int[]       mutantPercentageArray;
int[]       generationsArray;
int[]       populationSizeArray;

public void keyPressed() {
  /*****************fleetSizeArray****************/
  if (fleetSizeButton.sWitch) {
    fleetSizeButton.updateName();
    maxFleetSize = arrayToInt(fleetSizeButton.numArray);
    println ("maxCars: "+maxFleetSize);
  }
  
  if (vehicleCapacityButton.sWitch) {
    vehicleCapacityButton.updateName();
    maxVehicleCapacity = arrayToInt(vehicleCapacityButton.numArray);
    println ("maxVehicleCapacity: "+maxVehicleCapacity);
  }
  
  if (mutantPercentageButton.sWitch) {
    mutantPercentageButton.updateName();
    println("mutantPercentageButton.addName: "+mutantPercentageButton.addName);
    mutantPercent = arrayToInt(mutantPercentageButton.numArray)/100;
    println ("mutantPercent: "+mutantPercent);
  }
  
  if (generationsButton.sWitch) {
    generationsButton.updateName();
    numGen = arrayToInt(generationsButton.numArray);
    println ("numGen: "+numGen);
  } 
  
  if (populationSizeButton.sWitch) {
    populationSizeButton.updateName();
    populationSize = arrayToInt(populationSizeButton.numArray);
    println ("populationSize: "+populationSize);
  }
}

public int convertKey(char k){
  switch(k) {
  case '1': 
    return 1;
  case '2': 
    return 2;
  case '3': 
    return 3;
  case '4': 
    return 4;
  case '5': 
    return 5;
  case '6': 
    return 6;
  case '7': 
    return 7;
  case '8': 
    return 8;
  case '9': 
    return 9;
  case '0': 
    return 0;
  default:
    return 0;  
  }
}

public boolean isNumeral(char k){
  switch(k) {
  case '1': 
    return true;
  case '2': 
    return true;
  case '3': 
    return true;
  case '4': 
    return true;
  case '5': 
    return true;
  case '6': 
    return true;
  case '7': 
    return true;
  case '8': 
    return true;
  case '9': 
    return true;
  case '0': 
    return true;
  default:
    return false;  
  }
}

public void displayTestString ( ) {
  for (int i=0;i<testArray.length;i++){
    text(testArray[i],300+i*15,320);
  }
  for (int i=0;i<maxFleetSizeArray.length;i++){
    fleetSizeButton.name += maxFleetSizeArray[i];
    //text(maxFleetSizeArray[i],90+i*6,120);
  }
}
//class textEntryButton {
//  int x;
//  int y;
//  int bheight = -20;
//  int bwidth = 160;
//  String name;
//  int fillColor;
//  
//  Button (int xx, int yy, String n) {
//    x = xx;
//    y = yy;
//    name = n;
//    fillColor=255;
//  }
//  
//   Button (String n) {
//    x = 0;
//    y = 0;
//    name = n;
//    fillColor=255;
//  }
//  
//  void display () {
//    fill(fillColor);
//    stroke(0);
//    rect(x-5,y+5,bwidth,bheight);  
//    fill(0);
//    text(name,x,y);
//  }
//
//  void display (int xx, int yy) {
//    x = xx;
//    y = yy;
//    fill(fillColor);
//    stroke(0);
//    rect(x-5,y+5,bwidth,bheight);  
//    fill(0);
//    text(name, x,y);
//  }
//  
//  void update () {
//    if (isWithin()){
//      fillColor = 200;
//    } else {
//      fillColor = 255;
//    }
//  }
//
//  boolean isWithin() {
//    if (x-5 < mouseX && mouseX < x+bwidth && y+bheight < mouseY && mouseY < y+5) {
//      //println("true");
//      return true;
//    } 
//    else {
////      println("false");
//      return false;
//    } 
//  }
//} 

public void windowController () {
  if (mapImageButton.sWitch){
    image(mapImage,201,0,width-201,height);
  }
  
  if (mapDataButton.sWitch){
    for(int i=0;i<mnbprtodi.numStations;i++) {
      mnbprtodi.station[i].display();  
    }
  }
  
  if (fleetDataButton.sWitch){
    for(int i=0;i<fleet.numCars;i++) {
      fleet.car[i].display();
    }
  }  
  
  if (chooseSimsButton.sWitch){
    sims.display();
  } 
  
  if (populationDataButton.sWitch){
    for (int i=0;i<population.numPeople;i++){
      if (!population.person[i].in_line && !population.person[i].arrived && !population.person[i].in_car) {
        population.person[i].queue();
      }
    }
  }
 
 if (runButton.sWitch) {
    background(255);
    controller();
    text("running simulations...", 20, 390);
 } 
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#c0c0c0", "STSim" });
  }
}
