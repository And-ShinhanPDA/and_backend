package com.example.alert_module.notification.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record AlertEvent(
        Long alertId,
        Long userId,
        String stockCode,
        String title,
        Map<String, List<AlertConditionDto>> grouped
) implements Serializable {}