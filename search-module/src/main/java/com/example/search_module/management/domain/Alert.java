package com.example.search_module.management.domain;

import jakarta.persistence.*;
import lombok.*;

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
    private Long alertId;

    private Long userId;
    private Boolean isActived;
    private String title;
    private String stockCode;
    private Boolean isTriggered;
    private Boolean isConditionSearch;
    private LocalDateTime lastNotifiedAt;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertConditionManager> conditionManagers = new ArrayList<>();
}
