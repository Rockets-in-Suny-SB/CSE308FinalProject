package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;

import java.util.Map;
import java.util.Set;

public class DemoDistrictComparision {
    private Integer districtId;
    private Map<DemographicGroup,DistrictComparison> selectedDemoComparision;


    public DemoDistrictComparision(Integer districtId, Map<DemographicGroup, DistrictComparison> selectedDemoComparision) {
        this.districtId = districtId;
        this.selectedDemoComparision = selectedDemoComparision;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Map<DemographicGroup, DistrictComparison> getSelectedDemoComparision() {
        return selectedDemoComparision;
    }

    public void setSelectedDemoComparision(Map<DemographicGroup, DistrictComparison> selectedDemoComparsion) {
        this.selectedDemoComparision = selectedDemoComparsion;
    }

}
