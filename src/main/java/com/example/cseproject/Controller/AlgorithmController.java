package com.example.cseproject.Controller;


import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.Service.AlgorithmService;
import com.example.cseproject.Service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/setting")
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;
    @Autowired
    StateService stateService;

    @RequestMapping(value = "/phase0", method = RequestMethod.POST)
    public @ResponseBody
    Result runPhase0(@RequestParam String stateName,
                     @RequestParam String election,
                     @RequestParam Float populationThreshold,
                     @RequestParam Float blocThreshold) {
        return algorithmService.runPhase0(stateName, election, populationThreshold, blocThreshold);
    }

    @RequestMapping(value = "/phase1Param", method = RequestMethod.POST)
    public String setPhase1(@RequestParam Parameter parameter) {
        algorithmService.setPhase1(parameter);
        return "Parameters have been set";
    }

//    @RequestMapping(value = "/phase1", method = RequestMethod.POST)
//    public @ResponseBody
//    Result runPhase1(@RequestParam Parameter parameter) {
//        Result result = algorithmService.runPhase1();
//        return result;
//    }

    @RequestMapping(value = "/specifyMinorityPopulation", method = RequestMethod.POST)
    public @ResponseBody
    Result specifyMinorityPopulation(@RequestParam String stateName,
                                     @RequestParam String status,
                                     @RequestParam Float maximumPercentage,
                                     @RequestParam Float minimumPercentage,
                                     @RequestParam Set<String> minorityPopulations,
                                     @RequestParam Boolean isCombined) {
        algorithmService.specifyMinorityPopulation(maximumPercentage, minimumPercentage,
                minorityPopulations, isCombined);
        return algorithmService.getMinorityPopulation(stateName, status);
    }


}

