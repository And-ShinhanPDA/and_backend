package com.example.alert_module.preset.dto;

import java.util.List;

public record PresetRequest(
        String title,
        List<ConditionRequest> conditions
) {
    public record ConditionRequest(
            String indicator,
            Double threshold,
            Double threshold2
    ) {}
}
