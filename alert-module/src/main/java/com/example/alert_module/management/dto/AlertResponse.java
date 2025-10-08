package com.example.alert_module.management.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AlertResponse(
        Long alertId,
        String stockCode,
        String title,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ConditionResponse> conditions
) {
    public record ConditionResponse(
            Long alertConditionId,
            String indicator,
            String operator,
            Double threshold,
            String description
    ) {}
}

