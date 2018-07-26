void controller () {
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

void view0 () {
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

void view1 () {
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

void view2 () {
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

void view3 () {
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


