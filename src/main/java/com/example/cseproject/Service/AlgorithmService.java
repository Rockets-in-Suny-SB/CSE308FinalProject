package com.example.cseproject.Service;

import com.example.cseproject.Algorithm.Algorithm;
import com.example.cseproject.DataClasses.*;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.State;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Service
public class AlgorithmService {
    @Autowired
    StateService stateService;
    Algorithm algorithm;

    public AlgorithmService() {
        algorithm = new Algorithm();
        algorithm.setParameter(new Parameter());
    }


    public Result runPhase0(String stateName, String election, Float populationThreshold, Float blocThreshold) {
        System.out.println("before state");
//        State targetState = stateService.getState(StateName.valueOf(stateName.toUpperCase()),
//                State_Status.OLD).get();

//        algorithm.initDistrict(targetState);
//        algorithm.initPrecincts(targetState);
        State targetState = new State();
        System.out.println("before districts");
        targetState.setElection(Election.valueOf(election.toUpperCase()));
        targetState.setName(StateName.valueOf(stateName.toUpperCase()));
        Threshold threshold = new Threshold();
        threshold.setPopulationThreshold(populationThreshold);
        threshold.setBlocThreshold(blocThreshold);
        targetState.setThreshold(threshold);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<Integer, Precinct> precincts = mapper.readValue(ResourceUtils.getFile("classpath:"
                                      + targetState.getName().toString()+"_precincts.json"), new TypeReference<>(){});
            System.out.println(precincts.values().size());
            targetState.setPrecinctsJson(precincts);
            System.out.println("Read success");
        }catch (Exception e){
            System.out.println(e);
        }
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
        algorithm.setPhase1(parameter,stateService);
        Result r = new Result();
        r.addResult("Status", "OK");
        return r;
    }
    /* */
    public Queue<Result> runPhase2() {
        Parameter parameter = algorithm.getParameter();
        Queue<Result> phase2Result = algorithm.phase2(parameter.getWeights());
        return phase2Result;
    }

    public Result getOneMove() {
        if (algorithm.getPhase2Results().isEmpty()) {
            return null;
        }
        return algorithm.getPhase2Results().remove();
    }


    public String specifyMinorityPopulation(PopulationDistribution populationDistribution) {
        Parameter parameter = algorithm.getParameter();
        parameter.setCombined(populationDistribution.getIsCombined());
        parameter.setMaximumPercentage(populationDistribution.getMaximumPercentage());
        parameter.setMinimumPercentage(populationDistribution.getMinimumPercentage());
        Set<DemographicGroup> demographicGroups = new HashSet<>();
        Set<Set<DemographicGroup>> combinedDemGroup = new HashSet<>();
        for (String minority : populationDistribution.getMinorityPopulations()) {
            demographicGroups.add(DemographicGroup.valueOf(minority.toUpperCase()));
        }
        if (!populationDistribution.getIsCombined()) {
            parameter.setMinorityPopulations(demographicGroups);
            return null;
        }
        for (Set<String> group : populationDistribution.getCombinedGroup()){
            Set<DemographicGroup> demGroup = new HashSet<>();
            for (String s : group) {
                demGroup.add(DemographicGroup.valueOf(s.toUpperCase()));
            }
            combinedDemGroup.add(demGroup);
        }
        parameter.setMinorityPopulations(demographicGroups);
        parameter.setCombinedGroup(combinedDemGroup);
        algorithm.setParameter(parameter);
        return "successfully specify minority population";
    }

    public Result getMinorityPopulation(StateName stateName, State_Status state_status) {
        Parameter parameter = algorithm.getParameter();
        State targetState = stateService.getState(stateName, state_status).get();
        Set<MinorityPopulation> minorityPopulationResult = targetState.getPopulationDistribution(parameter);
        Result result = new Result();
        System.out.println(minorityPopulationResult);
        result.addResult("Minority Population Distribution Table", minorityPopulationResult);
        return result;
    }

    public Result gerrymanderingScore() {
        return algorithm.gerrymanderingScore();
    }

    public Result displayNewPopulationDistribution() {
        return algorithm.displayNewPopulationDistribution();
    }

}
