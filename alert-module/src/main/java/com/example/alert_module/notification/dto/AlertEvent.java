package com.example.alert_module.notification.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.Set;

@Builder
public record AlertEvent(
        Long alertId,
        Long userId,
        String stockCode,
        String companyName,
        String title,
        Set<String> categories,
        boolean isTriggered
) implements Serializable {}