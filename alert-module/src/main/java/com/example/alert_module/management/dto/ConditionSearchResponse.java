package com.example.alert_module.management.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ConditionSearchResponse(
        String stockCode,
        LocalDateTime triggerDate,
        Map<String, Object> values
) {}
