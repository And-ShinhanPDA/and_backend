package com.example.alert_module.management.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertDetailResponse {

    private Long alertId;
    private String title;
    private String stockCode;
    private Boolean isActived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Condition> conditions;
    private Boolean isPrice;
    private String aiFeedback;

    @Getter
    @AllArgsConstructor
    public static class Condition {
        private String category;
        private String indicator;
        private Double threshold;
        private Double threshold2;
        private String description;
    }
}
