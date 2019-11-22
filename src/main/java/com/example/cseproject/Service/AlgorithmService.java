package com.example.cseproject.Service;

import com.example.cseproject.Algorithm.Algorithm;
import com.example.cseproject.DataClasses.*;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AlgorithmService {
    @Autowired
    StateService stateService;
    Algorithm algorithm;


    public Result runPhase0(String stateName, String election, Float populationThreshold, Float blocThreshold) {
        State targetState = stateService.getState(StateName.valueOf(stateName.toUpperCase()),
                State_Status.OLD).get();
        System.out.println("okay");
        targetState.setElection(Election.valueOf(election.toUpperCase()));
        Threshold threshold = new Threshold();
        threshold.setPopulationThreshold(populationThreshold);
        threshold.setBlocThreshold(blocThreshold);
        targetState.setThreshold(threshold);
        Set<EligibleBloc> eligibleBlocs = targetState.findEligibleBlocs();
        Result result = new Result();
        result.addResult("Eligible Blocs", eligibleBlocs);
        return result;
    }

    public Result runPhase1() {
        Parameter parameter = algorithm.getParameter();
        Result phase1Result = algorithm.phase1(parameter);
        return phase1Result;
    }

    public Result setPhase1(Parameter parameter) {
        algorithm.setParameter(parameter);
        Result r = new Result();
        r.addResult("Status", "OK");
        return r;
    }

    public String specifyMinorityPopulation(float maximumPercentage,
                                            float minimumPercentage,
                                            Set<String> minorityPopulations,
                                            Boolean isCombined) {
        Parameter parameter = algorithm.getParameter();
        parameter.setCombined(isCombined);
        parameter.setMaximumPercentage(maximumPercentage);
        parameter.setMinimumPercentage(minimumPercentage);
        Set<DemographicGroup> demographicGroups = new HashSet<>();
        for (String minority : minorityPopulations) {
            demographicGroups.add(DemographicGroup.valueOf(minority.toUpperCase()));
        }
        parameter.setMinorityPopulations(demographicGroups);
        algorithm.setParameter(parameter);
        return "successfully specify minority population";
    }

    public Result getMinorityPopulation() {
        Parameter parameter = algorithm.getParameter();
        State targetState = stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()),
                State_Status.NEW).get();
        Set<MinorityPopulation> minorityPopulationResult = targetState.getPopulationDistribution(parameter);
        Result result = new Result();
        result.addResult("Minority Population Distribution Table", minorityPopulationResult);
        return result;
    }


}
