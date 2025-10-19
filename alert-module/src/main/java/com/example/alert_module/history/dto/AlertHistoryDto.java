package com.example.alert_module.history.dto;

import com.example.alert_module.history.entity.AlertHistory;
import java.time.LocalDateTime;

public record AlertHistoryDto(
        Long id,
        Long alertId,
        LocalDateTime createdAt,
        String indicatorSnapshot,
        StockDto stock
) {
    public static AlertHistoryDto from(AlertHistory entity) {
        boolean isConditionSearch = entity.getAlert() == null
                || entity.getAlert().getStockCode() == null
                || entity.getAlert().getStockCode().isBlank();

        String stockCode = isConditionSearch ? "조건검색" : entity.getAlert().getStockCode();

        return new AlertHistoryDto(
                entity.getId(),
                entity.getAlert() != null ? entity.getAlert().getId() : null,
                entity.getCreatedAt(),
                entity.getIndicatorSnapshot(),
                new StockDto(stockCode, isConditionSearch)
        );
    }

    public record StockDto(String stockCode, boolean isConditionSearch) {
    }
}