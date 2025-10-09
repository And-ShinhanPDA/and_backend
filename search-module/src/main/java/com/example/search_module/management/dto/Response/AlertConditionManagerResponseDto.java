package com.example.search_module.management.dto.Response;

import com.example.search_module.management.entity.AlertConditionManager;

public record AlertConditionManagerResponseDto(
        Long alertConditionId,
        Double threshold,
        Double threshold2,
        String indicator,
        String valueType,
        String dataScope,
        String description
) {
    public static AlertConditionManagerResponseDto from(AlertConditionManager manager) {
        var cond = manager.getAlertCondition();
        return new AlertConditionManagerResponseDto(
                cond.getId(),
                manager.getThreshold(),
                manager.getThreshold2(),
                cond.getIndicator(),
                cond.getValueType(),
                cond.getDataScope(),
                cond.getDescription()
        );
    }
}