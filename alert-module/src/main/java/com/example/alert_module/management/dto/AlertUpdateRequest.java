package com.example.alert_module.management.dto;

import java.util.List;

public record AlertUpdateRequest(
        String stockCode,
        String title,
        Boolean isActive,
        Boolean isPreset,
        List<ConditionRequest> conditions,
        String aiFeedback

) {
    public record ConditionRequest(
            String indicator,
            Double threshold,
            Double threshold2
    ) {}
}
