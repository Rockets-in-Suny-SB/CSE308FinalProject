package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.JoinFactor;

import java.util.HashMap;
import java.util.Map;

public class Threshold {
    private float populationThreshold;
    private float blocThreshold;
    private double majorityMinorityThreshold;
    private Map<JoinFactor, Double> joinFactorThreshold;
    public Threshold(){
        majorityMinorityThreshold=0.5;
        joinFactorThreshold=new HashMap<>();
        joinFactorThreshold.put(JoinFactor.COMPACTNESS,0.5);
        joinFactorThreshold.put(JoinFactor.EQUALPOPULATION,0.5);
        joinFactorThreshold.put(JoinFactor.POLITICALFAIRNESS,0.5);
        joinFactorThreshold.put(JoinFactor.SINGLECOUNTY,0.5);
    }
    public double getMajorityMinorityThreshold() {
        return majorityMinorityThreshold;
    }

    public void setMajorityMinorityThreshold(double majorityMinorityThreshold) {
        this.majorityMinorityThreshold = majorityMinorityThreshold;
    }

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

    public Map<JoinFactor, Double> getJoinFactorThreshold() {
        return joinFactorThreshold;
    }

    public void setJoinFactorThreshold(Map<JoinFactor, Double> joinFactorThreshold) {
        this.joinFactorThreshold = joinFactorThreshold;
    }
}
