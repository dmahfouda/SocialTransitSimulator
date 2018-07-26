void windowController () {
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
