
//*********************************************************************************************************

//this function connects station a to station b,
//calculates the distance between the two stations
//and stores that distance in the "distance_to_station" field
void connect (Station a, Station b) {
  a.linked_to_station = (Station[]) append(a.linked_to_station, b);
  float distance = dist(a.x,a.y,b.x,b.y);
  a.distance_to_station = append(a.distance_to_station, distance);
}


//this function connects station a to station b,
//calculates the distance between the two stations
//and stores that distance in the "distance_to_station" field
void connect_both(Station a, Station b) {
  //connect a to b
  a.linked_to_station = (Station[]) append(a.linked_to_station, b);
  float distance = dist(a.x,a.y,b.x,b.y);
  a.distance_to_station = append(a.distance_to_station, distance);

  //then connect b to a
  b.linked_to_station = (Station[]) append(b.linked_to_station, a);
  b.distance_to_station = append(b.distance_to_station, distance);
}



