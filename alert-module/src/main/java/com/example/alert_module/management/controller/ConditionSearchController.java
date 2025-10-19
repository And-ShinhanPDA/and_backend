package com.example.alert_module.management.controller;

import com.example.alert_module.management.dto.ConditionSearchResponse;
import com.example.alert_module.management.service.ConditionSearchService;
import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class ConditionSearchController {

    private final ConditionSearchService conditionSearchService;

    @GetMapping("/condition/{alertId}")
    public ResponseEntity<?> getResults(
            @AuthUser Long userId,
            @PathVariable Long alertId
    ) {
        log.info("🔎 [ConditionSearch] userId={}, alertId={} 결과 조회 요청", userId, alertId);

        List<ConditionSearchResponse> results = conditionSearchService.getConditionSearchResults(alertId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        ResponseCode.SUCCESS_GET_CONDITION_SEARCH_RESULTS,
                        results
                )
        );
    }

    @GetMapping("/condition/triggered")
    public ResponseEntity<String> getTriggeredConditions(@AuthUser Long userId) {
        conditionSearchService.logActiveConditionAlerts(userId);
        return ResponseEntity.ok("Condition-triggered alert logs printed successfully");
    }
}
