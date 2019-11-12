package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.Weights;

import java.util.Map;

public class Parameter {
    private int targetDistricts;
    private Map<Weights, Float> weights;
    private Boolean updateDiscrete;
    private DemograpicGroup minorityPopulation;
    private float maximumPercentage;
    private float minimumPercentage;
    private Boolean isCombined;
}
