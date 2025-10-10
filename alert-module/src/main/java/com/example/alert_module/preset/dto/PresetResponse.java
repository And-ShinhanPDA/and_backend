package com.example.alert_module.preset.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PresetResponse(
        Long presetId,
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
