package com.example.alert_module.preset.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PresetConditionId implements Serializable {

    @Column(name = "preset_id")
    private Long presetId;

    @Column(name = "alert_condition_id")
    private Long alertConditionId;
    public void setPresetId(Long presetId) { this.presetId = presetId; }
    public void setAlertConditionId(Long alertConditionId) { this.alertConditionId = alertConditionId; }

}
