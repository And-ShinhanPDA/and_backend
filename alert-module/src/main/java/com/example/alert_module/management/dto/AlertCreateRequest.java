package com.example.alert_module.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AlertCreateRequest(
        @NotBlank String stockCode,
        @NotBlank String title,
        @NotNull Boolean isActive,
        @NotNull Boolean isPreset,
        List<ConditionRequest> conditions,
        String aiFeedback
) {
    public record ConditionRequest(
            @NotBlank String indicator,
            Double threshold,
            Double threshold2
    ) {}
}
