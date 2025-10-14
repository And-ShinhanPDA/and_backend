package com.example.alert_module.evaluation.event.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AlertEvent(
        Long userId,
        String stockCode,
        String conditionType,
        LocalDateTime detectedAt
) implements Serializable {}