package com.example.cseproject.Controller;

import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.Service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RestController    // This means that this class is a Controller
@RequestMapping(path = "/state") // This means URL's start with /state (after Application path)
public class StateController {
    private final SimpMessagingTemplate template;
    @Autowired
    StateService stateService;

    @Autowired
    StateController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        try {
            File file = ResourceUtils.getFile("classpath:ohio_p0.json");
            String str = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            return str;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @RequestMapping(value = "/getDistrictData", method = RequestMethod.GET)
    public Result getDistrictsGeoJSON(@RequestParam String state, @RequestParam String year) {
        return stateService.getDistrictsData(state, year);
    }


    @MessageMapping(value = "/getPrecinctsData")
    public void getStatePrecinctsData(String state) {
        this.template.convertAndSend("/precincts", "You get precincts from web socket for " + state + " successfully!");
    }

}
