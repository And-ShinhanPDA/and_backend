package com.example.search_module.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AlertConditionManagerId implements java.io.Serializable {

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "alert_condition_id")
    private Long alertConditionId;
}
