package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Model.Precinct;

import java.util.Map;
import java.util.Set;

public class ClusterData {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer id;

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    public int getGopVotes() {
        return gopVotes;
    }

    public void setGopVotes(int gopVotes) {
        this.gopVotes = gopVotes;
    }

    public int getDemVotes() {
        return demVotes;
    }

    public void setDemVotes(int demVotes) {
        this.demVotes = demVotes;
    }

    private int gopVotes;
    private int demVotes;
    private int population;
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    public ClusterData(Cluster c){
        this.id=c.getId();
        this.population=c.getPopulation();
        this.minorityGroupPopulation=c.getMinorityGroupPopulation();
        this.gopVotes=c.getGopVotes();
        this.demVotes=c.getDemVotes();
    }
}
