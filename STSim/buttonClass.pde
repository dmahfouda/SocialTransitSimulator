class Button {
  int x;
  int y;
  int bheight = -20;
  int bwidth = 160;
  String name;
  int fillColor;
  String addName;
  boolean sWitch;
  boolean within;
  int[] numArray;

  Button (int xx, int yy, String n) {
    x = xx;
    y = yy;
    name = n;
    fillColor=255;
    addName = "";
    sWitch = false;
    within = false;
    numArray = new int[0];
  }

  Button (String n) {
    x = 0;
    y = 0;
    name = n;
    fillColor=255;
    addName = "";
    sWitch = false;
    numArray = new int[0];
  }

  void display () {
    fill(fillColor);
    stroke(0);
    rect(x-5,y+5,bwidth,bheight);  
    fill(0);
    text(name+addName, x,y);
  }

  void display (int xx, int yy) {
    x = xx;
    y = yy;
    fill(fillColor);
    stroke(0);
    rectMode(CORNER);
    rect(x-5,y+5,bwidth,bheight);  
    fill(0);    
    text(name+addName, x,y);
  }

  void update () {
    isWithin();
    if (within) {
      fillColor = 200;
    } 
    else {
      fillColor = 255;
    }
  }

  void isWithin() {
    if (x-5 < mouseX && mouseX < x+bwidth && y+bheight < mouseY && mouseY < y+5) {
      within = true;
    } 
    else {
      within = false;
    } 
  }

  void updateName () {
    if (sWitch) {
      if (isNumeral(key)){
        int y = convertKey(key);
        numArray = append(numArray,y);
      }
      if (key == DELETE || key == BACKSPACE) {
        if (numArray.length>0){
          numArray = shorten(numArray);
        }
      }

      String tempString = "";

      for (int i=0;i<numArray.length;i++){
        tempString += numArray[i];
      }

      addName = tempString;
    } 
  }
} 


