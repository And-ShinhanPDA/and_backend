// SearchServiceImpl.java
package com.example.search_module.management.service;

import com.example.search_module.management.dto.request.SearchReq;
import com.example.search_module.management.dto.request.SearchReq.AlertConditionRequest;
import com.example.search_module.management.dto.response.AlertResponseDto;
import com.example.search_module.management.entity.*;
import com.example.search_module.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchServiceImpl implements SearchService {

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;

    @Override
    public AlertResponseDto createAlert(SearchReq dto, Long userId) {

        // 1️⃣ Alert 생성
        Alert alert = Alert.builder()
                .userId(userId)
                .title(dto.title())
                .isActived(dto.isActive() != null ? dto.isActive() : true)
                .isTriggered(false)
                .isConditionSearch(!Boolean.TRUE.equals(dto.isPreset())) // 프리셋이면 false로
                .lastNotifiedAt(LocalDateTime.now())
                .build();

        alertRepository.save(alert);

        // 2️⃣ 각 indicator 기반으로 AlertConditionManager 생성
        for (AlertConditionRequest condReq : dto.conditions()) {
            AlertCondition condition = alertConditionRepository.findByIndicator(condReq.indicator())
                    .orElseThrow(() -> new RuntimeException("해당 indicator 조건을 찾을 수 없습니다: " + condReq.indicator()));

            AlertConditionManager manager = AlertConditionManager.builder()
                    .id(new AlertConditionManagerId(alert.getId(), condition.getId()))
                    .alert(alert)
                    .alertCondition(condition)
                    .threshold(condReq.threshold())
                    .threshold2(condReq.threshold2())
                    .build();

            alertConditionManagerRepository.save(manager);
            alert.getConditionManagers().add(manager);
        }

        return AlertResponseDto.from(alert);
    }
}
