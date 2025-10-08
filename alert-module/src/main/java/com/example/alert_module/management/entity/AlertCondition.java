package com.example.alert_module.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "alertCondition")
public class AlertCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_condition_id")
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "indicator", nullable = false)
    private String indicator;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

