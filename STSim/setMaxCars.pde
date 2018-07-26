void setMaxCars (int[] mCars) {
  maxFleetSize = 0;
  for (int i=0;i<mCars.length;i++) {
    maxFleetSize += (mCars[i]*pow(10,mCars.length-1-i));
  }
}

void setMaxCapacity (int[] mCap) {
  maxVehicleCapacity = 0;
  for (int i=0;i<mCap.length;i++) {
    maxVehicleCapacity += (mCap[i]*pow(10,mCap.length-1-i));
  }
}

int arrayToInt (int[] tempArray) {
  int tempInt = 0;
  for (int i=0;i<tempArray.length;i++) {
    tempInt += (tempArray[i]*pow(10,tempArray.length-1-i));
  }
  return tempInt;
}


