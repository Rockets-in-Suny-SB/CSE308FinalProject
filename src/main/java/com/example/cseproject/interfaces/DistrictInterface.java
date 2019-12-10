package com.example.cseproject.interfaces;

import java.util.Set;

public interface DistrictInterface<Precinct extends PrecinctInterface> {
    Integer getId();
    Set<Precinct> getPrecincts();
    void removePrecinct(Precinct p);
    void addPrecinct(Precinct p);
    Set<Precinct> getBorderPrecincts();
    Precinct getPrecinct(Integer precinctID);
    default Integer getPopulation() {
        return 0;
    }
}
