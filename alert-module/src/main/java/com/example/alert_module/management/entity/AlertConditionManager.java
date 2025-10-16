package com.example.alert_module.management.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "alertConditionManager")
public class AlertConditionManager {

    @EmbeddedId
    private AlertConditionManagerId id = new AlertConditionManagerId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alertId")
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alertConditionId")
    @JoinColumn(name = "alert_condition_id")
    private AlertCondition alertCondition;

    @Column(name = "threshold")
    private Double threshold;

    @Column(name = "threshold2")
    private Double threshold2;



    public static AlertConditionManager of(Alert alert, AlertCondition cond, Double t1, Double t2) {
        AlertConditionManager m = new AlertConditionManager();
        m.setAlert(alert);
        m.setAlertCondition(cond);
        m.setThreshold(t1);
        m.setThreshold2(t2);

        AlertConditionManagerId id = new AlertConditionManagerId();
        id.setAlertId(alert.getId());
        id.setAlertConditionId(cond.getId());
        m.setId(id);
        return m;
    }
}
