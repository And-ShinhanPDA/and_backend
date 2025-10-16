package com.example.alert_module.history.dto;

import com.example.alert_module.history.entity.AlertHistory;
import java.time.LocalDateTime;

public record AlertHistoryDto(
        Long id,
        Long alertId,
        String stockCode,
        Boolean isSent,
        String indicatorSnapshot,
        LocalDateTime createdAt
) {
    public static AlertHistoryDto from(AlertHistory entity) {
        return new AlertHistoryDto(
                entity.getId(),
                entity.getAlert().getId(),
                entity.getAlert().getStockCode(),
                entity.getIsSent(),
                entity.getIndicatorSnapshot(),
                entity.getCreatedAt()
        );
    }
}
