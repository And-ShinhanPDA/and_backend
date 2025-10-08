package com.example.alert_module.management.controller;

import com.example.alert_module.common.dto.ApiResponse;
import com.example.alert_module.common.util.JwtSimpleParser;
import com.example.alert_module.management.dto.AlertCreateRequest;
import com.example.alert_module.management.dto.AlertResponse;
import com.example.alert_module.management.service.AlertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public List<AlertResponse> getAlerts(
            @RequestParam(name = "stockCode", required = false) String stockCode,
            @RequestParam(name = "enabled", required = false) Boolean enabled,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = JwtSimpleParser.extractUserId(authHeader);

        return alertService.getAlerts(userId, stockCode, enabled);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @RequestBody AlertCreateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = JwtSimpleParser.extractUserId(authHeader);

        AlertResponse response = alertService.createAlert(userId, request);

        return ResponseEntity.ok(ApiResponse.success("알림 생성 성공", response));
    }
}
