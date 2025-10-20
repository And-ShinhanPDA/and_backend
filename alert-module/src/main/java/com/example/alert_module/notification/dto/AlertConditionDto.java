package com.example.alert_module.notification.dto;

import java.io.Serializable;

public record AlertConditionDto(
        String indicator,
        Double threshold,
        Double threshold2
) implements Serializable {}
