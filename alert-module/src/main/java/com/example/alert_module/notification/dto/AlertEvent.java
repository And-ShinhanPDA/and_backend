package com.example.alert_module.notification.dto;

import java.io.Serializable;
import java.util.Set;

public record AlertEvent(
        Long alertId,
        Long userId,
        String stockCode,
        String companyName,
        String title,
        Set<String> categories,
        boolean isTriggered
) implements Serializable {}