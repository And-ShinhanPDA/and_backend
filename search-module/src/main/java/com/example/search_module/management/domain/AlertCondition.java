package com.example.search_module.management.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_condition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertConditionId;

    private String indicator;    // e.g. "RSI", "SMA"
    private String operator;     // e.g. ">", "<", ">=", "<="
    private String description;
    private LocalDateTime createdAt;
}
