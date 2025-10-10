package com.example.alert_module.preset.dto;

import java.util.List;

public record PresetResponse(
        Long presetId,
        String title,
        String category,
        List<ConditionResponse> conditions
) {
    public record ConditionResponse(
            Long alertConditionId,
            String indicator,
            String operator,
            Double threshold,
            Double threshold2
    ) {}
}
