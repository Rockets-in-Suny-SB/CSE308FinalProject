package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;

public class MinorityPopulation {
    private String demographicGroup;
    private Float percentage;
    private Integer population;

    public String getDemographicGroup() {
        return demographicGroup;
    }

    public void setDemographicGroup(String demographicGroup) {
        this.demographicGroup = demographicGroup;
    }

    public MinorityPopulation(String demographicGroup, Float percentage, Integer population) {
        this.demographicGroup = demographicGroup;
        this.percentage = percentage;
        this.population = population;
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
