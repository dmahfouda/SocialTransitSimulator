class Organism {
  int num_traits;
  int trait_res;
  Trait[] trait;
  int fitness;
//  float mutantPercent = .2;
  Organism mom,dad;
  //spec traits
  //array to store mapped trait values
  int[] mappedTrait; 
  Simulator simulation;

  Organism (int num, int res) {
    num_traits = num;
    trait_res = res;    
    trait = new Trait[num_traits];
    mappedTrait = new int[num_traits];
    make();
  }

  Organism (Organism rmom, Organism rdad) {
    mom = rmom;
    dad = rdad;
    num_traits = mom.num_traits; 
    trait_res = mom.trait_res;    
    trait = new Trait[num_traits];
    mappedTrait = new int[num_traits];
    make();
    breed();
  }

  void make ( ) {
    for (int i=0;i<num_traits;i++) {
      trait[i] = new Trait(trait_res);
    }
  }

  void breed ( ) {
    //chose random crossover
    int crossover = floor(random(0,trait_res));

    for (int j=0;j<num_traits;j++) {
      for (int i=0;i<trait_res;i++) {
        if (i<crossover) {
          trait[j].gene[i] = mom.trait[j].gene[i];
        } 
        else {
          trait[j].gene[i] = dad.trait[j].gene[i];
        }
      }
    }

    mutate();
  }

  void mutate ( ) {
    float mTest = random(0,1);
    if (mTest<mutantPercent) {
      //mutate random gene
      trait[floor(random(0,num_traits))].gene[floor(random(0,trait_res))] = floor(random(0,10));
    }  
  }  

  void getFit ( ) {
    int fitness_factor = 1000;
    float lower_bound = 0;
//    println("maxVehicleCapacity: "+maxVehicleCapacity);
    float upper_bound = maxVehicleCapacity;
//    println ("here b");
    //(express each trait as num between lower and upper bound
    for (int i=0;i<num_traits;i++) {
      mappedTrait[i] = floor(map(trait[i].intValue(),0,pow(10,trait_res),lower_bound,upper_bound));
    }
//    println ("here c");
    //find fitness
    simulation = new Simulator(mappedTrait);
//    println ("here d");
    fitness = simulation.run();
//    println ("here e");  
  }

  void display () {
    for (int i=0;i<mappedTrait.length;i++) {
      int xx = 20; 
      //modulo should be a variable
      fill(0);
      if (i>4) {xx = 50;}
      rect(xx,(i%5)*10+20,mappedTrait[i],5);
      text(mappedTrait[i],xx+mappedTrait[i]+3,(i%5)*10+27);
    }
  }  
}


