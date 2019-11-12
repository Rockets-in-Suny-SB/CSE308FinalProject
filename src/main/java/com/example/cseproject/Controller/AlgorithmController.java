package com.example.cseproject.Controller;


import com.example.cseproject.Service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class AlgorithmController {
    @Autowired
    AlgorithmService algorithmService;


}

