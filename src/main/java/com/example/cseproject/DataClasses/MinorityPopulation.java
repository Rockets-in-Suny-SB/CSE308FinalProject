package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;

public class MinorityPopulation {
    private DemographicGroup demographicGroup;
    private Float percentage;
    private Integer population;

    public MinorityPopulation(DemographicGroup demographicGroup, Float percentage, Integer population) {
        this.demographicGroup = demographicGroup;
        this.percentage = percentage;
        this.population = population;
    }

    public DemographicGroup getDemographicGroup() {
        return demographicGroup;
    }

    public void setDemographicGroup(DemographicGroup demographicGroup) {
        this.demographicGroup = demographicGroup;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }
}
