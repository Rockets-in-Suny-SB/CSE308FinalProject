package com.example.cseproject.interfaces;


import java.util.Set;

public interface StateInterface<
        Precinct extends PrecinctInterface,
        District extends DistrictInterface<Precinct>> {
    Set<Precinct> getPrecincts();
    Set<District> getDistricts();

    Precinct getPrecinct(Integer precinctId);

    District getDistrict(Integer districtId);

    default int getPopulation() {
        return 0;
    }
}
