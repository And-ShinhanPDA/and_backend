package com.example.alert_module.management.dto;

import lombok.Builder;

@Builder
public record AlertPriceDto(Long id, Long userId, String stockCode, boolean isPrice) {
}
