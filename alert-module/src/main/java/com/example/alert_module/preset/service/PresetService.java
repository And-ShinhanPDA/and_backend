package com.example.alert_module.preset.service;

import com.example.alert_module.management.entity.AlertCondition;
import com.example.alert_module.management.repository.AlertConditionRepository;
import com.example.alert_module.preset.dto.PresetRequest;
import com.example.alert_module.preset.dto.PresetResponse;
import com.example.alert_module.preset.entity.Preset;
import com.example.alert_module.preset.entity.PresetCondition;
import com.example.alert_module.preset.repository.PresetConditionRepository;
import com.example.alert_module.preset.repository.PresetRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresetService {

    private final PresetRepository presetRepository;
    private final PresetConditionRepository presetConditionRepository;
    private final AlertConditionRepository alertConditionRepository;

    @Transactional
    public PresetResponse createPreset(Long userId, PresetRequest request) {
        log.info("🟢 [1] Preset 생성 시작 - userId={}, title={}", userId, request.title());

        Preset preset = Preset.builder()
                .userId(userId)
                .title(request.title())
                .category("custom")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        presetRepository.save(preset);
        log.info("✅ [2] Preset 저장 완료 - id={}, title={}, userId={}",
                preset.getId(), preset.getTitle(), preset.getUserId());

        // 조건 목록 처리
        Set<String> indicators = request.conditions().stream()
                .map(PresetRequest.ConditionRequest::indicator)
                .collect(Collectors.toSet());
        List<AlertCondition> condList = alertConditionRepository.findByIndicatorIn(indicators);
        Map<String, AlertCondition> condMap = condList.stream()
                .collect(Collectors.toMap(AlertCondition::getIndicator, c -> c));

        List<PresetResponse.ConditionResponse> conditionResponses = new ArrayList<>();

        for (var c : request.conditions()) {
            AlertCondition alertCondition = condMap.get(c.indicator());
            if (alertCondition == null) {
                throw new IllegalArgumentException("등록되지 않은 indicator: " + c.indicator());
            }

            PresetCondition presetCondition = new PresetCondition();
            presetCondition.setPreset(preset);
            presetCondition.setAlertCondition(alertCondition);
            presetCondition.setThreshold(c.threshold());
            presetCondition.setThreshold2(c.threshold2());
            presetConditionRepository.save(presetCondition);

            conditionResponses.add(new PresetResponse.ConditionResponse(
                    alertCondition.getId(),
                    alertCondition.getIndicator(),
                    null,
                    c.threshold(),
                    alertCondition.getDescription()
            ));
        }

        log.info("🎯 [7] 모든 PresetCondition 저장 완료 - presetId={}", preset.getId());

        // ✅ 프리셋 응답 DTO 구성
        return new PresetResponse(
                preset.getId(),
                preset.getTitle(),
                true,
                preset.getCreatedAt(),
                preset.getUpdatedAt(),
                conditionResponses
        );
    }

}
