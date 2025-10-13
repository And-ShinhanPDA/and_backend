package com.example.alert_module.history.controller;


import com.example.alert_module.history.dto.AlertHistoryDto;
import com.example.alert_module.history.dto.AlertHistoryPeriodReq;
import com.example.alert_module.history.service.AlertHistoryService;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertHistoryController {

    private final AlertHistoryService alertHistoryService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAlertHistories(@AuthUser Long userId) {
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_TODAY_ALERT, alertHistoryService.getTodayHistories(userId)));
    }


    @GetMapping("/history/{stockCode}")
    public ResponseEntity<?> getAlertHistories(
            @AuthUser Long userId,
            @PathVariable String stockCode,
            @ModelAttribute AlertHistoryPeriodReq period
    ) {
        List<AlertHistoryDto> result;
        ResponseCode responseCode;

        if (period.start() != null && period.end() != null) {
            System.out.println("특정 기간");
            result = alertHistoryService.getHistoriesByPeriod(userId, stockCode, period.start(), period.end());
            responseCode = ResponseCode.SUCCESS_ALERT_HISTORY_PERIOD;
        } else {
            System.out.println("전체");
            result = alertHistoryService.getAlertHistories(userId, stockCode);
            responseCode = ResponseCode.SUCCESS_ALERT_HISTORY_ALL;
        }

        return ResponseEntity.ok(ApiResponse.success(responseCode, result));
    }


    @GetMapping("/heatmap")
    public ResponseEntity<?> getHeatMap(@AuthUser Long userId) {
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS_ALERT_HEATMAP, alertHistoryService.getHeatMap(userId)));
    }


}
