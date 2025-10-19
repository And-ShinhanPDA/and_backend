package com.example.alert_module.management.dto;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record AlertResponse(
        Long alertId,
        String stockCode,
        String title,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ConditionResponse> conditions,
        @Nullable String aiFeedback
) {
    // ✅ 기존 코드와의 호환을 위한 추가 생성자 (기본 aiFeedback = null)
    public AlertResponse(
            Long id,
            String stockCode,
            String title,
            Boolean isActived,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<ConditionResponse> conditions
    ) {
        this(id, stockCode, title, isActived, createdAt, updatedAt, conditions, null);
    }

    public record ConditionResponse(
            Long alertConditionId,
            String indicator,
            Double threshold,
            Double threshold2,
            String description
    ) {}
}

