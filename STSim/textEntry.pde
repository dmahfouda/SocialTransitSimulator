int[]       testArray;
int[]       maxFleetSizeArray;
int[]       maxVehicleCapacityArray;
int[]       mutantPercentageArray;
int[]       generationsArray;
int[]       populationSizeArray;

void keyPressed() {
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

int convertKey(char k){
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

boolean isNumeral(char k){
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

void displayTestString ( ) {
  for (int i=0;i<testArray.length;i++){
    text(testArray[i],300+i*15,320);
  }
  for (int i=0;i<maxFleetSizeArray.length;i++){
    fleetSizeButton.name += maxFleetSizeArray[i];
    //text(maxFleetSizeArray[i],90+i*6,120);
  }
}
