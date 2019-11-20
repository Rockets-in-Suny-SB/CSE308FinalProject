package com.example.cseproject.Controller;

import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value="/getDistrictGeoJSON",method = RequestMethod.GET)
    public String getDistrictsGeoJSON(@RequestParam String state){
        return "DistrictsGeoJSON";
    }


    @MessageMapping(value="/getPrecinctsData")
    public void getStatePrecinctsData(String state){
        this.template.convertAndSend("/precincts","You get precincts from web socket for "+state+" successfully!");
    }

}
