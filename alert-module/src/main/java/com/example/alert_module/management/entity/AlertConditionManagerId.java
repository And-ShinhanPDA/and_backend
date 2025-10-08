package com.example.alert_module.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class AlertConditionManagerId implements Serializable {

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "alert_condition_id")
    private Long alertConditionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertConditionManagerId that)) return false;
        return Objects.equals(alertId, that.alertId)
                && Objects.equals(alertConditionId, that.alertConditionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId, alertConditionId);
    }
}
