class gaPopulation {
  int num_members;
  int num_traits;
  int trait_res;
  int x_trans,y_trans;
  int myGen;

  Organism best, worst; 
  float average; //average is not an organism

  Organism[] member;
  Organism[] parent;
  Organism[] child;

  gaPopulation (int num, int n_t, int t_r,int mG) {
    num_members = num;
    num_traits = n_t;
    trait_res = t_r;
    myGen = mG;

    member = new Organism[num_members];
    parent = new Organism[num_members];
    child = new Organism[num_members];

    x_trans = 0;
    y_trans = 0;

    make();
  }

  void make () {
    for (int i=0;i<num_members;i++) {
      member[i] = new Organism(num_traits, trait_res);
    }
  }

  void getFit() {
    for (int j=0;j<num_members;j++){
//      println("here a");
      member[j].getFit();
      println("getFit()");
    }
  }

  // gen is the number of generations
  void regenerate ( ) {
    //1.evaluate fitness of each member
    for (int j=0;j<num_members;j++){
//    controller();
      member[j].getFit();
      println("simulation no: "+j+" fitness = "+member[j].fitness);
    }

    //2.select parents - tournament method
    selectParents();

    //3.breed children -- mutation happens during breeding
    breedChildren();

    //4.set parents = children;
    gen++;
//    println("gen: "+gen);
    gafleet[gen] = new gaPopulation (num_members, num_traits, trait_res,gen);
//    println("gafleet[gen].myGen: "+gafleet[gen].myGen);
    gafleet[gen].member = child; 
//    println("gafleet[gen].myGen: "+gafleet[gen].myGen);
  }

  void selectParents () {
    for (int j=0;j<num_members;j++) {
      int rand1 = floor(random(0,num_members));
      int rand2 = floor(random(0,num_members));

      //evaluate fitness of two randomly selected memberss
      if (member[rand1].fitness > member[rand2].fitness) {
        parent[j] = member[rand1];
      } 
      else {
        parent[j] = member[rand2];
      }
    }
  }

  void breedChildren() {
    Organism randMom, randDad;
    int crossover;

    for (int i=0;i<num_members;i++){
      //randomly select two parents
      randMom = parent[floor(random(0,populationSize))];
      randDad = parent[floor(random(0,populationSize))];

      child[i] = new Organism (randMom, randDad);
    }
  }

  void getBest ( ) {
    int j = 0;
    for (int i=0;i<num_members;i++){
      if (member[j].fitness < member[i].fitness) { 
        j=i;
      }
    }
    best = member[j];
  }

  void getAverage ( ) {
    for (int i=0;i<num_members;i++){
      average += member[i].fitness;
    }

    average = average/num_members;
  }

  void getWorst ( ) {
    int j = 0;
    for (int i=0;i<num_members;i++){
      if (member[j].fitness > member[i].fitness) { 
        j=i;
      }
    }
    worst = member[j];  
  }

  void getTrends ( ) {
    getBest();
    getWorst();
    getAverage();

    println("best fitness = "+ best.fitness);

    bestArray[gen-1] = best.fitness;
    worstArray[gen-1] = worst.fitness;
    averageArray[gen-1] = average;
  }

  void displayBest ( ) {
    int thisGen = gen-1;
    x_trans = myGen%10;
    if (myGen > 9) {
      y_trans = 100;
    }
    if (myGen > 19) {
      y_trans = 200;
    }
    if (myGen > 29) {
      y_trans = 300;
    }
    if (myGen > 39) {
      y_trans = 400;
    }
    pushMatrix();
    stroke(0);
    strokeWeight(0);
    translate(200+x_trans*100,y_trans);
    best.display();
    text("gen:"+myGen+" fit:"+best.fitness,20,90);
    popMatrix();
  }
}    



