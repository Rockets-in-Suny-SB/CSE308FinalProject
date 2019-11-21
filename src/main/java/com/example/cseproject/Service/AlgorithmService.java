package com.example.cseproject.Service;

import com.example.cseproject.Algorithm.Algorithm;
import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;

@Service
public class AlgorithmService {
    @Autowired
    StateService stateService;
    Algorithm algorithm;
    public String setThreshold(Float populationThreshold, Float blocThreshold){
        Parameter parameter = algorithm.getParameter();
        State targetState=stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()),
                State_Status.NEW, parameter.getElection()).get();
        Threshold threshold = new Threshold();
        threshold.setBlocThreshold(blocThreshold);
        threshold.setPopulationThreshold(populationThreshold);
        targetState.setThreshold(threshold);
        return "Successfully set thresholds";
    }
    public Result runPhase0(){
        Parameter parameter = algorithm.getParameter();
        State targetState=stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()),
                State_Status.NEW, parameter.getElection()).get();
        Result phase0Result = algorithm.phase0(targetState.getThreshold());
        return phase0Result;
    }

    public Result runPhase1(){
        Parameter parameter = algorithm.getParameter();
        Result phase1Result =algorithm.phase1(parameter);
        return  phase1Result;
    }
    public Result setPhase1(Parameter parameter){
        algorithm.setParameter(parameter);
        Result r=new Result();
        r.addResult("Status","OK");
        return r;
    }

    public String specifyMinorityPopulation( float maximumPercentage,
                                             float minimumPercentage,
                                             Set<String> minorityPopulations,
                                             Boolean isCombined){


        Parameter parameter = algorithm.getParameter();
        parameter.setCombined(isCombined);
        parameter.setMaximumPercentage(maximumPercentage);
        parameter.setMinimumPercentage(minimumPercentage);
        Set<DemograpicGroup> demograpicGroups = new HashSet<>();
        for (String minority : minorityPopulations){
            demograpicGroups.add(DemograpicGroup.valueOf(minority.toUpperCase()));
        }
        parameter.setMinorityPopulations(demograpicGroups);
        algorithm.setParameter(parameter);
        return "successfully specify minority population";
    }
    public Result getMinorityPopulation(){
        Parameter parameter = algorithm.getParameter();
        State targetState=stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()),
                State_Status.NEW, parameter.getElection()).get();
        Set<Set<Object>> minorityPopulationResult = targetState.getPopulationDistribution(parameter);
        Result result = new Result();
        result.addResult("Minority Population Distribution Table", minorityPopulationResult);
        return result;
    }



}
