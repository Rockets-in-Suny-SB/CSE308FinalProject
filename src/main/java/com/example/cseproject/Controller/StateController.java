package com.example.cseproject.Controller;

import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.State;
import com.example.cseproject.Service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController    // This means that this class is a Controller
@RequestMapping(path="/state") // This means URL's start with /state (after Application path)
public class StateController {
    private final SimpMessagingTemplate template;
    @Autowired
    StateController(SimpMessagingTemplate template){
        this.template=template;
    }
    @Autowired
    StateService stateService;

    @RequestMapping(value="/getDistrictData",method = RequestMethod.GET)
    public @ResponseBody Result getDistrictsGeoJSON(@RequestParam String state, @RequestParam String election){
        State targetState=stateService.getState(StateName.OREGON, State_Status.OLD,Election.CONGRESSION_2016).get();
        Result r=new Result();
        System.out.println(targetState);
        for(District d:targetState.getDistricts()){
            System.out.println(d.getName());
            System.out.println(d.getPopulation());
            r.addResult(d.getName(),d);
        }
        return r;
    }


    @MessageMapping(value="/getPrecinctsData")
    public void getStatePrecinctsData(String state){
        this.template.convertAndSend("/precincts","You get precincts from web socket for "+state+" successfully!");
    }

}
