package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;

public class DistrictComparison {
    private Integer oldDistrictPopulation;
    private Integer newDistrictPopulation;
    private Float oldPercentage;
    private Float newPercentage;
    private Boolean increaseMoreThanTenPercent;

    public DistrictComparison(Integer oldDistrictPopulation, Integer newDistrictPopulation, Float oldPercentage,
                                                        Float newPercentage, Boolean increaseMoreThanTenPercent) {
        this.oldDistrictPopulation = oldDistrictPopulation;
        this.newDistrictPopulation = newDistrictPopulation;
        this.oldPercentage = oldPercentage;
        this.newPercentage = newPercentage;
        this.increaseMoreThanTenPercent = increaseMoreThanTenPercent;
    }

    public Integer getOldDistrictPopulation() {
        return oldDistrictPopulation;
    }

    public void setOldDistrictPopulation(Integer oldDistrictPopulation) {
        this.oldDistrictPopulation = oldDistrictPopulation;
    }

    public Integer getNewDistrictPopulation() {
        return newDistrictPopulation;
    }

    public void setNewDistrictPopulation(Integer newDistrictPopulation) {
        this.newDistrictPopulation = newDistrictPopulation;
    }

    public Float getOldPercentage() {
        return oldPercentage;
    }

    public void setOldPercentage(Float oldPercentage) {
        this.oldPercentage = oldPercentage;
    }

    public Float getNewPercentage() {
        return newPercentage;
    }

    public void setNewPercentage(Float newPercentage) {
        this.newPercentage = newPercentage;
    }

    public Boolean getIncreaseMoreThanTenPercent() {
        return increaseMoreThanTenPercent;
    }

    public void setIncreaseMoreThanTenPercent(Boolean increaseMoreThanTenPercent) {
        this.increaseMoreThanTenPercent = increaseMoreThanTenPercent;
    }
}
