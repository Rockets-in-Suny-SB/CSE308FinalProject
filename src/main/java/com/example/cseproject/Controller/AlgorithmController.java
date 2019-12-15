package com.example.cseproject.Controller;


import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.DataClasses.PopulationDistribution;
import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Service.AlgorithmService;
import com.example.cseproject.Service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Queue;
import java.util.Set;

@RestController
@RequestMapping("/setting")
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;
    @Autowired
    StateService stateService;


    @RequestMapping(value = "/preprocess", method = RequestMethod.GET)
    public @ResponseBody Result preprocess () {
        //pre-load data here
        Result result = new Result();
        return result;
    }


    @RequestMapping(value = "/phase0", method = RequestMethod.POST)
    public @ResponseBody Result runPhase0(@RequestParam String stateName,
                                          @RequestParam String election,
                                          @RequestParam Float populationThreshold,
                                          @RequestParam Float blocThreshold){
        Result result = algorithmService.runPhase0(stateName, election, populationThreshold, blocThreshold);
        return result;
    }
    @RequestMapping(value = "/phase1Param",method = RequestMethod.POST)
    public Result setPhase1(@RequestBody Parameter parameter){
        algorithmService.setPhase1(parameter);
        Result r=new Result();
        r.addResult("status","done");
        return r;
    }
    @RequestMapping(value = "/phase1", method = RequestMethod.POST)
    public @ResponseBody Result runPhase1(){
        Result result = algorithmService.runPhase1();
        return result;
    }

    @RequestMapping(value = "/phase2", method = RequestMethod.POST)
    public @ResponseBody Result runPhase2() {

        while (true) {
            Queue<Result> results = algorithmService.runPhase2();
            System.out.println(results.size());
            if (!results.isEmpty()){
                Result result = results.remove();
                Boolean isFinal = false;
                if (results.isEmpty())
                    isFinal = true;
                result.addResult("isFinal", isFinal);
                return result;
            }
        }

    }

    @RequestMapping(value = "/phase2Move", method = RequestMethod.POST)
    public @ResponseBody Result getOneMove() {
        return algorithmService.getOneMove();
    }


    @RequestMapping(value = "/specifyMinorityPopulation", method = RequestMethod.POST)
    public @ResponseBody Result specifyMinorityPopulation(@RequestBody PopulationDistribution populationDistribution) {
        algorithmService.specifyMinorityPopulation(populationDistribution);
        StateName stateName = StateName.valueOf(populationDistribution.getStateName().toUpperCase());
        State_Status state_status = State_Status.valueOf(populationDistribution.getStatus().toUpperCase());
        Result minorityPopulationResult = algorithmService.getMinorityPopulation(stateName,state_status);
        return minorityPopulationResult;
    }

    //Must call phase 2 before calling this method;
    @RequestMapping(value = "/DisplayMajorityMinorityResult", method = RequestMethod.GET)
    public @ResponseBody Result DisplayMajorityMinorityResult() {
        return algorithmService.DisplayMajorityMinorityResult();
    }
    @RequestMapping(value = "/GetPhaseTime", method = RequestMethod.GET)
    public @ResponseBody Result getPhaseTime(){
        return algorithmService.getPhaseTime();
    }


    @RequestMapping(value = "/gerrymandering", method = RequestMethod.POST)
    public @ResponseBody Result gerrymandering(){
        return algorithmService.gerrymanderingScore();
    }

    @RequestMapping(value = "/newPopulationDistribution", method = RequestMethod.POST)
    public @ResponseBody Result displayNewPopulationDistribution() {
        return algorithmService.displayNewPopulationDistribution();
    }




}

