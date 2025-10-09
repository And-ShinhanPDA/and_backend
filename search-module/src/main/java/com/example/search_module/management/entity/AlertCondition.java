package com.example.search_module.management.entity;

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
    @Column(name = "alert_condition_id")
    private Long id;

    private String category;
    private String dataScope;    // "minute" or "daily"
    private String valueType;    // ex) "rsi14", "price"
    private String indicator;    // ex) "RSI_OVER"
    private String description;
    private LocalDateTime createdAt;
}
