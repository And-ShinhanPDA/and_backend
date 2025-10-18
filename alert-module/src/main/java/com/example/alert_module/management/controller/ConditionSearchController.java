package com.example.alert_module.management.controller;

import com.example.alert_module.management.service.ConditionSearchService;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class ConditionSearchController {

    private final ConditionSearchService conditionSearchService;

    @GetMapping("/condition/{alertId}")
    public void getResults(
            @AuthUser Long userId,
            @PathVariable Long alertId
    ) {
        log.info("🔎 [ConditionSearch] userId={}, alertId={} 결과 조회 요청", userId, alertId);
        conditionSearchService.logGroupedIndicatorValues(alertId);
    }
}
