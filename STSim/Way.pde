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

  void wConnect(){
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

