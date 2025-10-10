package com.example.alert_module.preset.repository;

import com.example.alert_module.preset.entity.Preset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresetRepository extends JpaRepository<Preset, Long> {

    List<Preset> findByUserId(Long userId);
    List<Preset> findByUserIdAndTitleContaining(Long userId, String keyword);
}
