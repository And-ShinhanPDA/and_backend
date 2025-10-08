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
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAlerts(
            @RequestParam(name = "stockCode", required = false) String stockCode,
            @RequestParam(name = "enabled", required = false) Boolean enabled,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = JwtSimpleParser.extractUserId(authHeader);

        List<AlertResponse> response = alertService.getAlerts(userId, stockCode, enabled);

        return ResponseEntity.ok(ApiResponse.success("알림 리스트를 성공적으로 조회했습니다.", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @RequestBody AlertCreateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = JwtSimpleParser.extractUserId(authHeader);

        AlertResponse response = alertService.createAlert(userId, request);

        return ResponseEntity.ok(ApiResponse.success("알림을 성공적으로 생성했습니다.", response));
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<ApiResponse<String>> deleteAlert(
            @PathVariable Long alertId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = JwtSimpleParser.extractUserId(authHeader);
        alertService.deleteAlert(userId, alertId);

        return ResponseEntity.ok(ApiResponse.success("알림을 성공적으로 삭제했습니다.", null));
    }

}
