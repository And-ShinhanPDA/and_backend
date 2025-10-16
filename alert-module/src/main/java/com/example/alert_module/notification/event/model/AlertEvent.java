package com.example.alert_module.notification.event.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AlertEvent(
        Long userId,
        String stockCode
) implements Serializable {}