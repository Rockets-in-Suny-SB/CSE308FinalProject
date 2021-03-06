package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.Measure;

import java.util.Map;
import java.util.Set;

public class Parameter {

    private String stateName;
    private int targetDistricts;
    private Map<Measure, Double> weights;
    private Boolean updateDiscrete;


    private DemographicGroup targetMinorityPopulation;

    private Set<DemographicGroup> minorityPopulations;

    private float maximumPercentage;
    private float minimumPercentage;
    private Boolean isCombined;
    private Election election;
    private Set<Set<DemographicGroup>> combinedGroup;

    public DemographicGroup getTargetMinorityPopulation() {
        return targetMinorityPopulation;
    }

    public void setTargetMinorityPopulation(DemographicGroup targetMinorityPopulation) {
        this.targetMinorityPopulation = targetMinorityPopulation;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public int getTargetDistricts() {
        return targetDistricts;
    }

    public void setTargetDistricts(int targetDistricts) {
        this.targetDistricts = targetDistricts;
    }

    public Map<Measure, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<Measure, Double> weights) {
        this.weights = weights;
    }

    public Boolean getUpdateDiscrete() {
        return updateDiscrete;
    }

    public void setUpdateDiscrete(Boolean updateDiscrete) {
        this.updateDiscrete = updateDiscrete;
    }

    public Set<DemographicGroup> getMinorityPopulations() {
        return minorityPopulations;
    }

    public void setMinorityPopulations(Set<DemographicGroup> minorityPopulations) {
        this.minorityPopulations = minorityPopulations;
    }

    public float getMaximumPercentage() {
        return maximumPercentage;
    }

    public void setMaximumPercentage(float maximumPercentage) {
        this.maximumPercentage = maximumPercentage;
    }

    public float getMinimumPercentage() {
        return minimumPercentage;
    }

    public void setMinimumPercentage(float minimumPercentage) {
        this.minimumPercentage = minimumPercentage;
    }

    public Boolean getCombined() {
        return isCombined;
    }

    public void setCombined(Boolean combined) {
        isCombined = combined;
    }

    public Set<Set<DemographicGroup>> getCombinedGroup() {
        return combinedGroup;
    }

    public void setCombinedGroup(Set<Set<DemographicGroup>> combinedGroup) {
        this.combinedGroup = combinedGroup;
    }
}
