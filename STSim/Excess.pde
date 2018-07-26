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
