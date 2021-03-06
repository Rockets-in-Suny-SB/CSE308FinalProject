package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.Model.District;

import java.util.Map;

public class DistrictData {
    private String name;
    private Integer population;

    private Map<PartyName, Integer> partyVotes;

    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    private Map<DemographicGroup, Integer> minorityGroupPopulation;

    public DistrictData(District district) {
        this.name = district.getName();
        this.population = district.getPopulation();
        this.partyVotes = district.getPartyVotes();
        this.minorityGroupPopulation = district.getMinorityGroupPopulation();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Map<PartyName, Integer> getPartyVotes() {
        return partyVotes;
    }

    public void setPartyVotes(Map<PartyName, Integer> partyVotes) {
        this.partyVotes = partyVotes;
    }


}
