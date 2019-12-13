package com.example.cseproject.interfaces;

import java.util.Set;

public interface PrecinctInterface {
    Integer getId();

    //Object getGeometry();

    Integer getOriginalDistrictID();

    Set<Integer> getNeighborIds();

    Integer getPopulation();

    Integer getGopVote();

    Integer getDemVote();
}
