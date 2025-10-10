package com.example.alert_module.preset.service;

import com.example.alert_module.management.entity.AlertCondition;
import com.example.alert_module.management.repository.AlertConditionRepository;
import com.example.alert_module.preset.dto.PresetRequest;
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
    public Long createPreset(Long userId, PresetRequest request) {
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

        if (request.conditions() == null || request.conditions().isEmpty()) {
            log.warn("⚠️ [3] 조건이 비어 있습니다. 프리셋만 생성됩니다.");
            return preset.getId();
        }

        Set<String> indicators = request.conditions().stream()
                .map(PresetRequest.ConditionRequest::indicator)
                .collect(Collectors.toSet());
        log.info("🟢 [4] 요청으로부터 indicator 목록 추출 완료: {}", indicators);

        List<AlertCondition> condList = alertConditionRepository.findByIndicatorIn(indicators);
        Map<String, AlertCondition> condMap = condList.stream()
                .collect(Collectors.toMap(AlertCondition::getIndicator, c -> c));

        log.info("✅ [5] AlertCondition 조회 완료 - {}건", condMap.size());
        condMap.forEach((k, v) ->
                log.info("    └ indicator={}, id={}", k, v.getId())
        );

        for (var c : request.conditions()) {
            log.info("🟡 [6] PresetCondition 생성 시도 - indicator={}, threshold={}, threshold2={}",
                    c.indicator(), c.threshold(), c.threshold2());

            AlertCondition alertCondition = condMap.get(c.indicator());
            if (alertCondition == null) {
                log.error("❌ [6] AlertCondition 조회 실패: {}", c.indicator());
                throw new IllegalArgumentException("등록되지 않은 indicator: " + c.indicator());
            }

            try {
                PresetCondition presetCondition = new PresetCondition();
                presetCondition.setPreset(preset);
                presetCondition.setAlertCondition(alertCondition);
                presetCondition.setThreshold(c.threshold());
                presetCondition.setThreshold2(c.threshold2());

                log.debug("    🔹 Before save - preset.id={}, alertCondition.id={}, idObj={}",
                        preset.getId(),
                        alertCondition.getId(),
                        presetCondition.getId());

                presetConditionRepository.save(presetCondition);

                log.info("✅ [6] PresetCondition 저장 성공 - presetId={}, alertConditionId={}, indicator={}",
                        preset.getId(), alertCondition.getId(), alertCondition.getIndicator());
            } catch (Exception e) {
                log.error("🔥 [6] PresetCondition 저장 중 오류 발생 - indicator={}, message={}",
                        c.indicator(), e.getMessage(), e);
                throw e;
            }
        }

        log.info("🎯 [7] 모든 PresetCondition 저장 완료 - presetId={}", preset.getId());
        return preset.getId();
    }
}
