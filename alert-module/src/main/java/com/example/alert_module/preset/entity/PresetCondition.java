package com.example.alert_module.preset.entity;

import com.example.alert_module.management.entity.AlertCondition;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "presetCondition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresetCondition {

    @EmbeddedId
    private PresetConditionId id = new PresetConditionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("presetId")
    @JoinColumn(name = "preset_id")
    private Preset preset;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("alertConditionId")
    @JoinColumn(name = "alert_condition_id")
    private AlertCondition alertCondition;

    private Double threshold;
    private Double threshold2;
}
