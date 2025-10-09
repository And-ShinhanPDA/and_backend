package com.example.alert_module.history.controller;


import com.example.alert_module.history.service.AlertHistoryService;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertHistoryController {

    private final AlertHistoryService alertHistoryService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAlertHistories(@AuthUser Long userId) {
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_TODAY_ALERT, alertHistoryService.getTodayHistoryByUser(userId)));
    }



}
