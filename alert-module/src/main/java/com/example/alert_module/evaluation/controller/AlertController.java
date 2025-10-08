package com.example.alert_module.evaluation.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class AlertController {

    @GetMapping("/alerts")
    public String testAlert() {
        return "Alert service is running!";
    }

    @GetMapping("/a")
    public String testAlert2() {
        return "Alert service is running!";
    }
}
