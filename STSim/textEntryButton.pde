//class textEntryButton {
//  int x;
//  int y;
//  int bheight = -20;
//  int bwidth = 160;
//  String name;
//  int fillColor;
//  
//  Button (int xx, int yy, String n) {
//    x = xx;
//    y = yy;
//    name = n;
//    fillColor=255;
//  }
//  
//   Button (String n) {
//    x = 0;
//    y = 0;
//    name = n;
//    fillColor=255;
//  }
//  
//  void display () {
//    fill(fillColor);
//    stroke(0);
//    rect(x-5,y+5,bwidth,bheight);  
//    fill(0);
//    text(name,x,y);
//  }
//
//  void display (int xx, int yy) {
//    x = xx;
//    y = yy;
//    fill(fillColor);
//    stroke(0);
//    rect(x-5,y+5,bwidth,bheight);  
//    fill(0);
//    text(name, x,y);
//  }
//  
//  void update () {
//    if (isWithin()){
//      fillColor = 200;
//    } else {
//      fillColor = 255;
//    }
//  }
//
//  boolean isWithin() {
//    if (x-5 < mouseX && mouseX < x+bwidth && y+bheight < mouseY && mouseY < y+5) {
//      //println("true");
//      return true;
//    } 
//    else {
////      println("false");
//      return false;
//    } 
//  }
//} 

