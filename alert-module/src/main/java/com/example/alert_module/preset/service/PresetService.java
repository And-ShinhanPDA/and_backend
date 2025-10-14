package com.example.alert_module.preset.service;

import com.example.alert_module.common.exception.CustomException;
import com.example.alert_module.common.exception.ErrorCode;
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

        return new PresetResponse(
                preset.getId(),
                preset.getTitle(),
                preset.getCategory(),
                true,
                preset.getCreatedAt(),
                preset.getUpdatedAt(),
                conditionResponses
        );
    }

    @Transactional
    public void deletePreset(Long userId, Long presetId) {
        log.info("🗑️ [1] 프리셋 삭제 시도 - userId={}, presetId={}", userId, presetId);

        Preset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프리셋입니다."));

        if (!preset.getUserId().equals(userId)) {
            log.error("🚫 [2] 삭제 권한 없음 - preset.userId={}, request.userId={}", preset.getUserId(), userId);
            throw new IllegalStateException("본인 소유의 프리셋만 삭제할 수 있습니다.");
        }

        log.info("🧩 [3] s연결된 PresetCondition 삭제 시작 - presetId={}", presetId);
        presetConditionRepository.deleteAllByPreset(preset);
        log.info("✅ [3] PresetCondition 삭제 완료");

        presetRepository.delete(preset);
        log.info("✅ [4] Preset 삭제 완료 - presetId={}", presetId);
    }

    @Transactional(readOnly = true)
    public List<PresetResponse> getAllPresets(Long userId) {
        log.info("📋 [1] 프리셋 목록 조회 시작 - userId={}", userId);

        List<Long> targetUserIds = List.of(0L, userId);
        List<Preset> presets = presetRepository.findByUserIdIn(targetUserIds);

        if (presets.isEmpty()) {
            log.warn("⚠️ [2] 조회된 프리셋이 없습니다.");
            return List.of();
        }

        log.info("✅ [2] 조회된 프리셋 수: {}", presets.size());

        List<PresetResponse> responses = new ArrayList<>();

        for (Preset preset : presets) {
            List<PresetCondition> presetConditions = presetConditionRepository.findAllByPreset(preset);

            List<PresetResponse.ConditionResponse> conditionResponses = presetConditions.stream()
                    .map(pc -> new PresetResponse.ConditionResponse(
                            pc.getAlertCondition().getId(),
                            pc.getAlertCondition().getIndicator(),
                            null,
                            pc.getThreshold(),
                            pc.getAlertCondition().getDescription()
                    ))
                    .toList();

            responses.add(new PresetResponse(
                    preset.getId(),
                    preset.getTitle(),
                    preset.getCategory(),
                    true,
                    preset.getCreatedAt(),
                    preset.getUpdatedAt(),
                    conditionResponses
            ));
        }

        log.info("🎯 [3] 프리셋 변환 완료 - {}건", responses.size());
        return responses;
    }

    @Transactional
    public PresetResponse updatePreset(Long userId, Long presetId, PresetRequest request) {
        log.info("✏️ [1] 프리셋 수정 시작 - userId={}, presetId={}", userId, presetId);

        Preset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프리셋입니다."));

        if (!preset.getUserId().equals(userId)) {
            log.error("🚫 [2] 권한 없음 - preset.userId={}, request.userId={}", preset.getUserId(), userId);
            throw new IllegalStateException("본인 소유의 프리셋만 수정할 수 있습니다.");
        }

        log.info("🧩 [3] 기존 PresetCondition 삭제 시작 - presetId={}", presetId);
        presetConditionRepository.deleteAllByPreset(preset);
        log.info("✅ [3] 기존 조건 삭제 완료");

        preset.setTitle(request.title());
        preset.setUpdatedAt(LocalDateTime.now());
        presetRepository.save(preset);

        log.info("🟢 [4] 프리셋 기본정보 수정 완료 - title={}, updatedAt={}", preset.getTitle(), preset.getUpdatedAt());

        Set<String> indicators = request.conditions().stream()
                .map(PresetRequest.ConditionRequest::indicator)
                .collect(Collectors.toSet());
        List<AlertCondition> condList = alertConditionRepository.findByIndicatorIn(indicators);
        Map<String, AlertCondition> condMap = condList.stream()
                .collect(Collectors.toMap(AlertCondition::getIndicator, c -> c));

        List<PresetResponse.ConditionResponse> conditionResponses = new ArrayList<>();

        for (var c : request.conditions()) {
            AlertCondition alertCondition = condMap.get(c.indicator());
            if (alertCondition == null)
                throw new IllegalArgumentException("등록되지 않은 indicator: " + c.indicator());

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

        log.info("✅ [5] 모든 조건 재등록 완료 - presetId={}", presetId);

        return new PresetResponse(
                preset.getId(),
                preset.getTitle(),
                preset.getCategory(),
                true,
                preset.getCreatedAt(),
                preset.getUpdatedAt(),
                conditionResponses
        );
    }

    @Transactional(readOnly = true)
    public PresetResponse getPresetById(Long presetId) {

        Preset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESET_NOT_FOUND));

        List<PresetResponse.ConditionResponse> conditions =
                presetConditionRepository.findByPresetId(presetId)
                        .stream()
                        .map(pc -> new PresetResponse.ConditionResponse(
                                pc.getAlertCondition().getId(),
                                pc.getAlertCondition().getIndicator(),
                                null,
                                pc.getThreshold(),
                                pc.getAlertCondition().getDescription()
                        ))
                        .toList();

        return new PresetResponse(
                preset.getId(),
                preset.getTitle(),
                preset.getCategory(),
                true, // isActive는 엔티티에 없으므로 임시 true
                preset.getCreatedAt(),
                preset.getUpdatedAt(),
                conditions
        );
    }

}
