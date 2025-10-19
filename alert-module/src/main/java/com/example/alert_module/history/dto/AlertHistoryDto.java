package com.example.alert_module.history.dto;

import com.example.alert_module.history.entity.AlertHistory;
import java.time.LocalDateTime;

public record AlertHistoryDto(
        Long id,
        Long alertId,
        LocalDateTime createdAt,
        String indicatorSnapshot,
        String stockCode
) {
    public static AlertHistoryDto from(AlertHistory entity) {
        return new AlertHistoryDto(
                entity.getId(),
                entity.getAlert().getId(),
                entity.getCreatedAt(),
                entity.getIndicatorSnapshot(),
                entity.getAlert().getStockCode() != null  ? entity.getAlert().getStockCode() : "조건검색"
        );
    }
}