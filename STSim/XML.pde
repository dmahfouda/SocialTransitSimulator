void populate_xml () {//simulation
  proxml.XMLElement xml_simulation = new proxml.XMLElement("simulation");
  xml_simulations.addChild(xml_simulation);  

  //population    
  proxml.XMLElement xml_population = new proxml.XMLElement("population");
  xml_simulation.addChild(xml_population);

  for (int i=0;i<population.populationxml.getChildCount();i++){      
    proxml.XMLElement xml_person = new proxml.XMLElement("person");
    xml_person.addAttribute("name",population.person[i].name);
    xml_population.addChild(xml_person);  

    proxml.XMLElement xml_itinerary = new proxml.XMLElement("itinerary");
    xml_itinerary.addAttribute("start",population.person[i].start.name);
    xml_itinerary.addAttribute("destination",population.person[i].destination.name);
    xml_person.addChild(xml_itinerary);

    proxml.XMLElement xml_transport_info = new proxml.XMLElement("transport_info");
    xml_person.addChild(xml_transport_info);

    proxml.XMLElement xml_transport_time = new proxml.XMLElement("transport_time");
    xml_transport_info.addChild(xml_transport_time);

    proxml.XMLElement xml_wait_time = new proxml.XMLElement("wait_time");
    xml_wait_time.addAttribute("time_finish", population.person[i].wait_end);
    xml_wait_time.addAttribute("time_start", population.person[i].wait_start);
    xml_transport_time.addChild(xml_wait_time);

    proxml.XMLElement xml_travel_time = new proxml.XMLElement ("travel_time");
    xml_travel_time.addAttribute("time_finish", population.person[i].travel_end);
    xml_travel_time.addAttribute("time_start", population.person[i].travel_start);
    xml_transport_time.addChild(xml_travel_time);

    proxml.XMLElement xml_travel_distance = new proxml.XMLElement("travel_distance");
    xml_travel_distance.addAttribute("distance", population.person[i].travel_distance);
    xml_transport_info.addChild(xml_travel_distance);
  }

  //mmnbprtodi
  proxml.XMLElement xml_mnbprtodi = new proxml.XMLElement("mnbprtodi");
  xml_simulation.addChild(xml_mnbprtodi);

  //station
  for (int i=0;i<mnbprtodi.station.length;i++) {
    proxml.XMLElement xml_station = new proxml.XMLElement("station");

    xml_station.addAttribute("name", mnbprtodi.station[i].name);
    xml_station.addAttribute("x", mnbprtodi.station[i].x);
    xml_station.addAttribute("y", mnbprtodi.station[i].y);
    for (int j=0;j<mnbprtodi.station[i].linked_to_station.length;j++) {
      xml_station.addAttribute("linked_to_station"+j,mnbprtodi.station[i].linked_to_station[j].name);
    }
    xml_mnbprtodi.addChild(xml_station);
  }  

  //fleet
  proxml.XMLElement xml_fleet = new proxml.XMLElement("fleet");
  xml_simulation.addChild(xml_fleet);

  for(int i=0;i<fleet.car.length;i++){
    proxml.XMLElement xml_car = new proxml.XMLElement("car");

    xml_car.addAttribute("name",fleet.car[i].name);
    //THIS FIELD IS WEIRD _ WE ARE LOOKING FOR THE START CONFIGURATION
    //FOR CARS, BUT HAVEN'T FOUND IT YET
    xml_car.addAttribute("location",fleet.car[i].next.name);//car starts at previous destination -- this isn't always true
    xml_car.addAttribute("capacity",fleet.car[i].capacity); 
    xml_car.addAttribute("speed",fleet.car[i].speed);
    xml_car.addAttribute("travel_distance",fleet.car[i].travel_distance);
    //car.addAttribute("beahavior",behavior);

    //car.addAttribute("efficiency",efficiency);
    //car.addAttribute("operating_time",operating_time);

    xml_fleet.addChild(xml_car);
  }

  //efficiencies
  proxml.XMLElement xml_efficiency = new proxml.XMLElement("efficiency");
  xml_simulation.addChild(xml_efficiency);

  proxml.XMLElement xml_fitness = new proxml.XMLElement("fitness");

  float total_vehicle_cost = 0;
  float total_travel_time = 0;
  float total_waste = 0;
  float fitness_constant = 10000;

  for (int i=0;i<population.person.length;i++) {
    total_travel_time += population.person[i].travel_end - population.person[i].wait_start; 
  }

  for (int i=0;i<fleet.car.length;i++) {
    total_waste += fleet.car[i].waste; 
    total_vehicle_cost += fleet.car[i].capacity*100;
  }
  
  float cost = total_travel_time+total_waste+total_vehicle_cost;

  float fitness = floor(fitness_constant - (total_travel_time+total_waste+total_vehicle_cost));
  
  xml_fitness.addAttribute("totalTravelTime", total_travel_time);
  xml_fitness.addAttribute("totalVehicleCost", total_vehicle_cost);
  xml_fitness.addAttribute("fitness",fitness);
  xml_fitness.addAttribute("cost",cost);

  xml_efficiency.addChild(xml_fitness);
}

