package com.example.cseproject.Controller;


import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.Service.AlgorithmService;
import com.example.cseproject.Service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setting")
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;
    @Autowired
    StateService stateService;
    @RequestMapping(value = "/phase0", method = RequestMethod.POST)
    public String getThresholds(@RequestParam Float populationThreshold, @RequestParam Float blocThreshold){
        algorithmService.setThreshold(populationThreshold, blocThreshold);
        algorithmService.runPhase0();
        return "Thresholds have been set";
    }

    @RequestMapping(value="/specifyMinorityPopulation",method = RequestMethod.POST)
    public String specifyMinorityPopulation( @RequestParam float maximumPercentage,
                                             @RequestParam float minimumPercentage,
                                             @RequestParam List<String> minorityPopulations,
                                             @RequestParam Boolean isCombined){
        algorithmService.specifyMinorityPopulation(maximumPercentage, minimumPercentage,
                                                        minorityPopulations, isCombined);
        Result minorityPopulationResult = algorithmService.getMinorityPopulation();
        return "successfully specify minority population";
    }


}

