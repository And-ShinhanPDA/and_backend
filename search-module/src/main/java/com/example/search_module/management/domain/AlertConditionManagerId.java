package com.example.search_module.management.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertConditionManagerId implements Serializable {
    private Long alertId;
    private Long alertConditionId;
}
