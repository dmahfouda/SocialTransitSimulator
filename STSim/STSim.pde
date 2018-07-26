//I'm just changing this so that git has something to track
import proxml.*;

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
void setup () {
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

void xmlEvent(proxml.XMLElement element){
  xml_simulations = element;
  xmlInOut.saveElement(xml_simulations,"simulations.xml"); 
}


/*********************************************************************************************/
void draw () {
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

void displayTrends ( ) {
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

void displayInitialBest ( ) {
  stroke(0);
  line(500,0,500,400);
  translate(500,0);
  gafleet[0].best.display();
}

/****************************************************/
void display_waste(){
  int gap = floor(height/iterations);

  for (int i=0;i<iterations;i++){
    fill(255,0,0,75);
    rectMode(CORNER);
    rect(0,gap*i,map(total_waste[i],0,100000,0,width),gap/2);
    fill(0,255,0,75);
    rect(0,gap*i+gap/2,map(total_travel_time[i],0,50000,0,width),gap/2);
  }
}





