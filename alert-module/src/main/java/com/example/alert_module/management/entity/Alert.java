package com.example.alert_module.management.entity;

import com.example.alert_module.history.entity.AlertHistory;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "alert")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_actived")
    private Boolean isActived;

    @Column(name = "title")
    private String title;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "last_notified_at")
    private LocalDateTime lastNotifiedAt;

    @Column(name = "is_triggered")
    private Boolean isTriggered;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AlertHistory> alertHistories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlertConditionManager> conditionManagers = new ArrayList<>();


}
