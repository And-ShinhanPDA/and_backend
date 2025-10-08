package com.example.alert_module.management.service;

import com.example.alert_module.management.dto.AlertCreateRequest;
import com.example.alert_module.management.dto.AlertResponse;
import com.example.alert_module.management.repository.*;
import com.example.alert_module.management.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;

    public AlertService(AlertRepository alertRepository,
                        AlertConditionRepository alertConditionRepository,
                        AlertConditionManagerRepository alertConditionManagerRepository) {
        this.alertRepository = alertRepository;
        this.alertConditionRepository = alertConditionRepository;
        this.alertConditionManagerRepository = alertConditionManagerRepository;
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
