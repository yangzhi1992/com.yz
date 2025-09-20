package com.commons.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gatewayAdmin")
public class AdminController {
    
    @GetMapping("/internal/api")
    public String internalApi() {
        return "Hello from internal API";
    }
}
