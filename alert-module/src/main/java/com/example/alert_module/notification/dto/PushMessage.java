package com.example.alert_module.notification.dto;

import lombok.Builder;

@Builder
public record PushMessage(
        String title,
        String body
) {}
