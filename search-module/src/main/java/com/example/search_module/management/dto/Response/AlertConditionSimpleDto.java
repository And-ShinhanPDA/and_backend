package com.example.search_module.management.dto.Response;

import com.example.search_module.management.entity.AlertCondition;

// AlertConditionSimpleDto.java
public record AlertConditionSimpleDto(
        Long alertConditionId,
        String category,
        String indicator,
        String valueType,
        String dataScope,
        String description
) {
    public static AlertConditionSimpleDto from(AlertCondition cond) {
        return new AlertConditionSimpleDto(
                cond.getId(),
                cond.getCategory(),
                cond.getIndicator(),
                cond.getValueType(),
                cond.getDataScope(),
                cond.getDescription()
        );
    }
}
