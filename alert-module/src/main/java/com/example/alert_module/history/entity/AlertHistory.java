package com.example.alert_module.history.entity;


import com.example.alert_module.management.entity.Alert;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alertHistory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @Column(name = "indicator_snapshot", columnDefinition = "JSON")
    private String indicatorSnapshot;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
