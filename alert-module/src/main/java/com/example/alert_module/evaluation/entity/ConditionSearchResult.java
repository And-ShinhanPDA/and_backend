package com.example.alert_module.evaluation.entity;

import com.example.alert_module.management.entity.Alert;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "condition_search_result")
public class ConditionSearchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "is_triggered")
    private Boolean isTriggered;

    @Column(name = "trigger_date")
    private LocalDateTime triggerDate;
}
