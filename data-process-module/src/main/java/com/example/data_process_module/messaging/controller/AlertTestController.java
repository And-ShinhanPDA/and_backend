package com.example.data_process_module.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.data_process_module.messaging.service.AlertSenderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class AlertTestController {

    private final AlertSenderService alertSenderService;

    @GetMapping("/send")
    public String sendTest(@RequestParam(defaultValue = "Hello Alert!") String msg) {
        alertSenderService.sendTestMessage(msg);
        return "Message sent: " + msg;
    }
}