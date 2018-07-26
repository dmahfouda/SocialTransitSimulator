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

  void display() {
    fill(0,0,255,50);
    int scrn = screen.height-50;
    float y = map(cost,0,10000,0,scrn);
    rect(0,scrn - y,(screen.width-260)/otherChildren, y);
  }
  
  void display(int x) {
    fill(0,0,255,50);
    noStroke();
    int scrn = screen.height - 50;
    float y = map(cost,0,10000,0,scrn);
    rect(x,scrn - y,(screen.width-300)/otherChildren, y);
    stroke(255);
    text("cost = total_travel_time+total_waste+total_vehicle_cost = "+cost, x, scrn-(y+5));
  }

}
