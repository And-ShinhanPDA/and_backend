package com.example.search_module.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    private Long userId;

    @Builder.Default
    private Boolean isActived = true;

    private String title;

    private String stockCode;

    @Builder.Default
    private Boolean isTriggered = false;

    @Builder.Default
    private Boolean isConditionSearch = true;

    private LocalDateTime lastNotifiedAt;

    @Builder.Default
    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertConditionManager> conditionManagers = new ArrayList<>();
}
