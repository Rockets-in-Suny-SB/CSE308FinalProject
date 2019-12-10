package com.example.cseproject.phase2.algorithm;



import com.example.cseproject.interfaces.DistrictInterface;
import com.example.cseproject.interfaces.PrecinctInterface;

import java.util.HashSet;
import java.util.Set;

public interface MeasureFunction<Precinct extends PrecinctInterface, District extends DistrictInterface<Precinct>> {
    double calculateMeasure(District district);
    default Set<MeasureFunction<Precinct, District>> subMeasures() {
        return new HashSet<>();
    }
}
