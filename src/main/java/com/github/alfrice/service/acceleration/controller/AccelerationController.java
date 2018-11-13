package com.github.alfrice.service.acceleration.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class AccelerationController {

    @Value("${canned.response}")
    String initialResponse;
    
    @RequestMapping("/")
    public String index() {
        return initialResponse;
    }
    
}
