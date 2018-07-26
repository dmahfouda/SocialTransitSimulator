class Trait {
  int trait_res;
  int[] gene;

  Trait(int t_r) {
    trait_res = t_r;
    gene = new int[trait_res];
    make();
  }

  void make () {
    for (int i=0;i<trait_res;i++) {
      gene[i] = floor(random(0,10));
    }
  }

  int intValue ( ) {
    int power = trait_res;
    int intval = 0;
    for (int i=0;i<trait_res;i++) {
      intval += gene[i]*10^power--;
    }
    return intval;
  }
}


