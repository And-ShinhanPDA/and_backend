package com.example.search_module.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_condition_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertConditionManager {

    @EmbeddedId
    private AlertConditionManagerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alertId")
    private Alert alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alertConditionId")
    private AlertCondition alertCondition;

    private Double threshold; // 예: RSI > 70
}
