package com.example.alert_module.history.controller;


import com.example.alert_module.history.dto.AlertHistoryDto;
import com.example.alert_module.history.service.AlertHistoryService;

import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertHistoryController {

    private final AlertHistoryService alertHistoryService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAlertHistories(@AuthUser Long userId) {
        var histories = alertHistoryService.getTodayHistoryByUser(userId)
                .stream()
                .map(AlertHistoryDto::from)
                .toList();
        return ResponseEntity.ok(histories);
    }



}
