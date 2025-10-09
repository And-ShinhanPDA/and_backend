package com.example.search_module.management.dto.Response;
import com.example.search_module.management.entity.Alert;
import java.time.LocalDateTime;
import java.util.List;

public record AlertResponseDto(
        Long alertId,
        String title,
        String stockCode,
        Boolean isActived,
        Boolean isTriggered,
        Boolean isConditionSearch,
        LocalDateTime lastNotifiedAt,
        List<AlertConditionManagerResponseDto> conditions
) {
    public static AlertResponseDto from(Alert alert) {
        return new AlertResponseDto(
                alert.getId(),
                alert.getTitle(),
                alert.getStockCode(),
                alert.getIsActived(),
                alert.getIsTriggered(),
                alert.getIsConditionSearch(),
                alert.getLastNotifiedAt(),
                alert.getConditionManagers().stream()
                        .map(AlertConditionManagerResponseDto::from)
                        .toList()
        );
    }
}