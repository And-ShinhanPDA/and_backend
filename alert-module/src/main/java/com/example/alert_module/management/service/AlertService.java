package com.example.alert_module.management.service;

import com.example.alert_module.management.dto.AlertCreateRequest;
import com.example.alert_module.management.dto.AlertResponse;
import com.example.alert_module.management.repository.*;
import com.example.alert_module.management.entity.*;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;

    public List<AlertResponse> getAlerts(Long userId, String stockCode, Boolean enabled) {
        List<Alert> alerts;

        if (stockCode != null && enabled != null) {
            alerts = alertRepository.findByUserIdAndStockCodeAndIsActived(userId, stockCode, enabled);
        } else if (stockCode != null) {
            alerts = alertRepository.findByUserIdAndStockCode(userId, stockCode);
        } else if (enabled != null) {
            alerts = alertRepository.findByUserIdAndIsActived(userId, enabled);
        } else {
            alerts = alertRepository.findByUserId(userId);
        }

        if (alerts.isEmpty()) return List.of();

        // ✅ 모든 alertId 한 번에 모으기
        List<Long> alertIds = alerts.stream().map(Alert::getId).toList();

        // ✅ 관련된 AlertConditionManager + AlertCondition 한 번에 조회
        List<AlertConditionManager> managers =
                alertConditionManagerRepository.findByAlertIdsWithCondition(alertIds);

        // ✅ alertId별로 conditions 매핑
        Map<Long, List<AlertResponse.ConditionResponse>> conditionMap = managers.stream()
                .collect(Collectors.groupingBy(
                        acm -> acm.getAlert().getId(),
                        Collectors.mapping(acm -> new AlertResponse.ConditionResponse(
                                acm.getAlertCondition().getId(),
                                acm.getAlertCondition().getIndicator(),
                                null, // operator (필요시 추가)
                                acm.getThreshold(),
                                acm.getThreshold2(),
                                acm.getAlertCondition().getDescription()
                        ), Collectors.toList())
                ));

        // ✅ AlertResponse 생성
        return alerts.stream()
                .map(alert -> new AlertResponse(
                        alert.getId(),
                        alert.getStockCode(),
                        alert.getTitle(),
                        alert.getIsActived(),
                        alert.getCreatedAt(),
                        alert.getUpdatedAt(),
                        conditionMap.getOrDefault(alert.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public AlertResponse createAlert(Long userId, AlertCreateRequest request) {
        Alert alert = new Alert();
        alert.setUserId(userId);
        alert.setTitle(request.title());
        alert.setStockCode(request.stockCode());
        alert.setIsActived(request.isActive());
        alert.setIsTriggered(false);
        alert.setIsConditionSearch(false);
        alertRepository.save(alert);

        Set<String> indicators = new HashSet<>();
        for (var c : request.conditions()) indicators.add(c.indicator());
        List<AlertCondition> condList = alertConditionRepository.findByIndicatorIn(indicators);
        Map<String, AlertCondition> condMap = new HashMap<>();
        for (var ac : condList) condMap.put(ac.getIndicator(), ac);

        List<AlertResponse.ConditionResponse> conditionResponses = new ArrayList<>();

        for (var c : request.conditions()) {
            AlertCondition cond = condMap.get(c.indicator());
            if (cond == null)
                throw new IllegalArgumentException("등록되지 않은 indicator: " + c.indicator());

            AlertConditionManager acm = new AlertConditionManager();
            acm.setAlert(alert);
            acm.setAlertCondition(cond);
            acm.setThreshold(c.threshold());
            acm.setThreshold2(c.threshold2());
            alertConditionManagerRepository.save(acm);

            conditionResponses.add(
                    new AlertResponse.ConditionResponse(
                            cond.getId(),
                            cond.getIndicator(),
                            null,
                            c.threshold(),
                            c.threshold2(),
                            cond.getDescription()
                    )
            );
        }

        return new AlertResponse(
                alert.getId(),
                alert.getStockCode(),
                alert.getTitle(),
                alert.getIsActived(),
                alert.getCreatedAt(),
                alert.getUpdatedAt(),
                conditionResponses
        );
    }

}
