package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.Weights;

import java.util.Map;

public class Parameter {

    private String stateName;
    private int targetDistricts;
    private Map<Weights, Float> weights;
    private Boolean updateDiscrete;
    private DemograpicGroup minorityPopulation;
    private float maximumPercentage;
    private float minimumPercentage;
    private Boolean isCombined;

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

    public Map<Weights, Float> getWeights() {
        return weights;
    }

    public void setWeights(Map<Weights, Float> weights) {
        this.weights = weights;
    }

    public Boolean getUpdateDiscrete() {
        return updateDiscrete;
    }

    public void setUpdateDiscrete(Boolean updateDiscrete) {
        this.updateDiscrete = updateDiscrete;
    }

    public DemograpicGroup getMinorityPopulation() {
        return minorityPopulation;
    }

    public void setMinorityPopulation(DemograpicGroup minorityPopulation) {
        this.minorityPopulation = minorityPopulation;
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
}
