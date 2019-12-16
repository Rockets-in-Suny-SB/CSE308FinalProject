package com.example.cseproject.Service;

import com.example.cseproject.Algorithm.Algorithm;
import com.example.cseproject.DataClasses.*;
import com.example.cseproject.Enum.*;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.State;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class AlgorithmService {
    @Autowired
    StateService stateService;
    Algorithm algorithm;
    double phase0Time;
    public AlgorithmService() {
        algorithm = new Algorithm();
        algorithm.setParameter(new Parameter());
    }


    public Result runPhase0(String stateName, String election, Float populationThreshold, Float blocThreshold) {
//        long startTime = System.nanoTime();
//        System.out.println("before state");
//        State targetState = stateService.getState(StateName.valueOf(stateName.toUpperCase()),
//                State_Status.OLD).get();
//
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
//        //Timer End
//        long endTime = System.nanoTime();
//        long elapsedTime = endTime-startTime;
//        double seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
//        phase0Time=seconds;
//        //Log time
//        Logger logger = Logger.getLogger("MyLog");
//        FileHandler fh;
//
//        try {
//
//            // This block configure the logger with handler and formatter
//            fh = new FileHandler((getClass().getClassLoader().getResource(".").getFile() + "/log/MyLogFile.log").replaceFirst("/",""));
//            logger.addHandler(fh);
//            SimpleFormatter formatter = new SimpleFormatter();
//            fh.setFormatter(formatter);
//
//            // the following statement is used to log any messages
//            logger.info("Phase 0 time:"+seconds);
//
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


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
        Map<Measure, Double> weights = parameter.getWeights();
        Set<Measure> badMeasures = new HashSet<>();
        for (Map.Entry<Measure, Double> entry : weights.entrySet()) {
            if (entry.getValue() <= 0)
                badMeasures.add(entry.getKey());
        }
        for (Measure m : badMeasures){
            weights.remove(m);
        }
        System.out.println(weights.size());
        Map<Measure, Double> r = new HashMap<>();
        r.put(Measure.CONVEX_HULL_COMPACTNESS, 0.6);
        Queue<Result> phase2Result = algorithm.phase2(r);
        return phase2Result;
    }

    public Result getOneMove() {
        if (algorithm.getPhase2Results().isEmpty()) {
            Result result = new Result();
            result.addResult("isFinal",true);
            return result;
        }

        Result result = algorithm.getPhase2Results().remove();
        if (algorithm.getPhase2Results().isEmpty()) {
            result.addResult("isFinal", true);
        }
        else {
            result.addResult("isFinal", false);
        }
        return result;
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

    public Result DisplayMajorityMinorityResult() {
        return algorithm.calculateMajorityMinorityDistrictData(stateService);
    }
    public Result getPhaseTime(){
        Result r=new Result();
        r.addResult("p0Time",phase0Time);
        r.addResult("p1Time",algorithm.phase1Time);
        r.addResult("p2Time",algorithm.phase2Time);
        return r;
    }


    public Result gerrymanderingScore() {
        return algorithm.gerrymanderingScore();
    }

    public Result displayNewPopulationDistribution() {
        return algorithm.displayNewPopulationDistribution();
    }


}
