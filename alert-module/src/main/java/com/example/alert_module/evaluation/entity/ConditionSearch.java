package com.example.alert_module.evaluation.entity;

import com.example.alert_module.management.entity.Alert;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "condition_search")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ConditionSearchId.class)
public class ConditionSearch {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @Id
    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "is_triggered")
    private Boolean isTriggered;

    @Column(name = "trigger_date")
    private LocalDateTime triggerDate;
}

