package com.example.alert_module.history.dto;

import com.example.alert_module.history.entity.AlertHistory;
import java.time.LocalDateTime;

public record AlertHistoryDto(
        Long id,
        LocalDateTime createdAt,
        String indicatorSnapshot,
        String stockCode
) {
    public static AlertHistoryDto from(AlertHistory entity) {
        return new AlertHistoryDto(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getIndicatorSnapshot(),
                entity.getAlert() != null ? entity.getAlert().getStockCode() : null
        );
    }
}