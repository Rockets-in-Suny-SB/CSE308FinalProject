package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.JoinFactor;

import java.util.Map;

public class Threshold {
    private float populationThreshold;
    private float blocThreshold;
    private Map<JoinFactor, Float> joinFactorThreshold;


    public float getPopulationThreshold() {
        return populationThreshold;
    }

    public void setPopulationThreshold(float populationThreshold) {
        this.populationThreshold = populationThreshold;
    }

    public float getBlocThreshold() {
        return blocThreshold;
    }

    public void setBlocThreshold(float blocThreshold) {
        this.blocThreshold = blocThreshold;
    }

    public Map<JoinFactor, Float> getJoinFactorThreshold() {
        return joinFactorThreshold;
    }

    public void setJoinFactorThreshold(Map<JoinFactor, Float> joinFactorThreshold) {
        this.joinFactorThreshold = joinFactorThreshold;
    }
}
