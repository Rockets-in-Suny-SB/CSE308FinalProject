package com.example.cseproject.phase2.measures;

import java.util.Set;

public interface PrecinctInterface {
    String getID();

    //Object getGeometry();

    String getOriginalDistrictID();

    Set<String> getNeighborIDs();

    int getPopulation();

    int getGOPVote();

    int getDEMVote();
}
