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
    @MapsId("alertId") // ⚠️ 필드명과 정확히 맞춰야 함
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alertConditionId") // ⚠️ 동일하게 맞춰야 함
    @JoinColumn(name = "alert_condition_id")
    private AlertCondition alertCondition;

    private Double threshold;
    private Double threshold2;
}
