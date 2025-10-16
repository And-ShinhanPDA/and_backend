package com.example.alert_module.evaluation.entity;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConditionSearchId implements Serializable {
    private Long alert;
    private String stockCode;
}
