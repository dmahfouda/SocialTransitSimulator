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
  void findpath(Station a, Station b) {
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

  void display() {
    stroke(255,0,0);
    strokeWeight(3);
    for (int i=1;i<path.length;i++) {
      line(path[i-1].x,path[i-1].y,path[i].x,path[i].y);
    }
  }

  boolean find_closest(Station[] s) {
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





