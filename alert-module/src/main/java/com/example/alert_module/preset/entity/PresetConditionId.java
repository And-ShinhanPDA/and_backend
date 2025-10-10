package com.example.alert_module.preset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresetConditionId implements Serializable {
    @Column(name = "preset_id")
    private Long presetId;

    @Column(name = "alert_condition_id")
    private Long alertConditionId;
}

