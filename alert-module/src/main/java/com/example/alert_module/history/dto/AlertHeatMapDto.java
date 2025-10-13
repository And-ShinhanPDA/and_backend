package com.example.alert_module.history.dto;

import java.util.List;

public class AlertHeatMapDto {

    public record HeatMapAlertDto(
            String stockCode,
            Long alertCount,
            Double price // null 허용 (현재 미사용)
    ) {}

    public record HeatMapResponseDto(
            List<HeatMapAlertDto> alerts,
            Long totalCount
    ) {}
}
