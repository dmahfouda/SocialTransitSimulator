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
  void connectStations () {
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




