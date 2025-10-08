package com.example.alert_module.management.controller;

import com.example.alert_module.management.dto.AlertCreateRequest;
import com.example.alert_module.management.dto.AlertResponse;
import com.example.alert_module.management.service.AlertService;
import com.example.alert_module.common.dto.ApiResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @RequestBody AlertCreateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromJwt(authHeader);
        AlertResponse response = alertService.createAlert(userId, request);
        return ResponseEntity.ok(ApiResponse.success("알림 생성 성공", response));
    }


    private Long extractUserIdFromJwt(String authHeader) {
        // TODO: 실제 JWT 파싱 로직으로 대체
        return 1L;
    }
}
