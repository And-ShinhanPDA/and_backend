package com.example.alert_module.management.controller;

import com.example.alert_module.common.dto.ApiResponse;
import com.example.alert_module.management.dto.*;
import com.example.alert_module.management.service.AlertService;
import com.example.user_module.common.security.AuthUser;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/{alertId}")
    public ResponseEntity<ApiResponse<AlertDetailResponse>> getAlertDetail(
            @PathVariable Long alertId,
            @AuthUser Long userId
    ) {
        AlertDetailResponse response = alertService.getAlertDetail(userId, alertId);
        return ResponseEntity.ok(ApiResponse.success("알림을 성공적으로 조회했습니다.",response));
    }

    @PatchMapping("/{alertId}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleAlert(
            @PathVariable Long alertId,
            @RequestBody ToggleRequest request,
            @AuthUser Long userId
    ) {
        alertService.toggleAlert(userId, alertId, request.isActived());
        return ResponseEntity.ok(ApiResponse.success("알림 상태가 변경되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAlerts(
            @RequestParam(name = "stockCode", required = false) String stockCode,
            @RequestParam(name = "enabled", required = false) Boolean enabled,
            @AuthUser Long userId
    ) {
        List<AlertResponse> response = alertService.getAlerts(userId, stockCode, enabled);

        return ResponseEntity.ok(ApiResponse.success("알림 리스트를 성공적으로 조회했습니다.", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AlertResponse>> createAlert(
            @RequestBody AlertCreateRequest request,
            @AuthUser Long userId) {

        AlertResponse response = alertService.createAlert(userId, request);

        return ResponseEntity.ok(ApiResponse.success("알림을 성공적으로 생성했습니다.", response));
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<ApiResponse<String>> deleteAlert(
            @PathVariable Long alertId,
            @AuthUser Long userId
    ) {
        alertService.deleteAlert(userId, alertId);

        return ResponseEntity.ok(ApiResponse.success("알림을 성공적으로 삭제했습니다.", null));
    }

    @PatchMapping("/{alertId}")
    public ResponseEntity<ApiResponse<AlertResponse>> updateAlert(
            @PathVariable Long alertId,
            @RequestBody AlertUpdateRequest request,
            @AuthUser Long userId
    ) {

        AlertResponse response = alertService.updateAlert(userId, alertId, request);

        return ResponseEntity.ok(ApiResponse.success("알림이 성공적으로 수정되었습니다.", response));
    }

    @GetMapping("/triggered")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> triggerAlert(@AuthUser Long userId) {
        List<AlertResponse> response = alertService.triggerAlert(userId);

        return ResponseEntity.ok(ApiResponse.success("현재 조건을 만족 중인 알림들이 조회되었습니다.", response));
    }

    @PatchMapping("/price/{stockCode}")
    public ResponseEntity<ApiResponse<AlertPriceDto>> togglePriceAlert(
            @PathVariable String stockCode,
            @RequestBody TogglePriceRequest request,
            @AuthUser Long userId
    ) {
        AlertPriceDto dto = alertService.togglePriceAlert(userId, stockCode, request.isTogglePrice());
        return ResponseEntity.ok(ApiResponse.success("시가/종가 알림 설정이 변경되었습니다.", dto));
    }

    @GetMapping("/price/{stockCode}")
    public ResponseEntity<ApiResponse<?>> getOrCreatePriceAlert(
            @AuthUser Long userId,
            @PathVariable String stockCode
    ) {

        return ResponseEntity.ok(ApiResponse.success("조회 성공", alertService.getOrCreatePriceAlert(userId, stockCode)));
    }


}
