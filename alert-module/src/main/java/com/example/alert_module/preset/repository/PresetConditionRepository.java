package com.example.alert_module.preset.repository;

import com.example.alert_module.preset.entity.PresetCondition;
import com.example.alert_module.preset.entity.PresetConditionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresetConditionRepository extends JpaRepository<PresetCondition, PresetConditionId> {

    List<PresetCondition> findByPreset_Id(Long presetId);
    List<PresetCondition> findByAlertCondition_Id(Long alertConditionId);
}
