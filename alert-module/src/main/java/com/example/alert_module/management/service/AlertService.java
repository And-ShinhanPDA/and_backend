package com.example.alert_module.management.service;

import com.example.alert_module.common.exception.CustomException;
import com.example.alert_module.common.exception.ErrorCode;
import com.example.alert_module.management.dto.*;
import com.example.alert_module.management.repository.*;
import com.example.alert_module.management.entity.*;
import jakarta.annotation.Nullable;
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
//    private final OpenAIService openAIService;

    @Transactional
    public AlertDetailResponse getAlertDetail(Long userId, Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_NOT_FOUND));

        if (!alert.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        List<AlertConditionManager> managers =
                alertConditionManagerRepository.findByAlertId(alertId);

        List<AlertDetailResponse.Condition> conditionResponses = managers.stream()
                .map(m -> {
                    AlertCondition cond = m.getAlertCondition();
                    return new AlertDetailResponse.Condition(
                            cond.getCategory(),
                            cond.getIndicator(),
                            m.getThreshold(),
                            m.getThreshold2(),
                            cond.getDescription()
                    );
                })
                .toList();

        return new AlertDetailResponse(
                alert.getId(),
                alert.getTitle(),
                alert.getStockCode(),
                alert.getIsActived(),
                alert.getCreatedAt(),
                alert.getUpdatedAt(),
                conditionResponses,
                alert.getAiFeedback()
        );
    }

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

        List<Long> alertIds = alerts.stream().map(Alert::getId).toList();

        List<AlertConditionManager> managers =
                alertConditionManagerRepository.findByAlertIdsWithCondition(alertIds);

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

        return alerts.stream()
                .map(alert -> new AlertResponse(
                        alert.getId(),
                        alert.getStockCode(),
                        alert.getTitle(),
                        alert.getIsActived(),
                        alert.getCreatedAt(),
                        alert.getUpdatedAt(),
                        conditionMap.getOrDefault(alert.getId(), List.of()),
                        alert.getAiFeedback()
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
        // ✅ AI 피드백 로직 추가
        String indicatorsSummary = conditionResponses.stream()
                .map(c -> String.format("- %s: %.2f ~ %.2f", c.indicator(), c.threshold(), c.threshold2()))
                .collect(Collectors.joining("\n"));

        // OpenAI 호출
//        String aiFeedback = openAIService.getAIFeedback(indicatorsSummary);

        // ✅ 2. DB에도 aiFeedback 저장
//        alert.setAiFeedback(aiFeedback);
        alertRepository.save(alert);

        return new AlertResponse(
                alert.getId(),
                alert.getStockCode(),
                alert.getTitle(),
                alert.getIsActived(),
                alert.getCreatedAt(),
                alert.getUpdatedAt(),
                conditionResponses,
                alert.getAiFeedback()
        );
    }

    @Transactional
    public void deleteAlert(Long userId, Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!alert.getUserId().equals(userId)) {
            throw new IllegalStateException("본인 소유의 알림만 삭제할 수 있습니다.");
        }

        alertConditionManagerRepository.deleteAllByAlertId(alertId);

        alertRepository.delete(alert);
    }

    @Transactional
    public AlertResponse updateAlert(Long userId, Long alertId, AlertUpdateRequest request) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!alert.getUserId().equals(userId)) {
            throw new IllegalStateException("본인 소유의 알림만 수정할 수 있습니다.");
        }

        alert.setTitle(request.title());
        alert.setStockCode(request.stockCode());
        alert.setIsActived(request.isActive());
        alertRepository.save(alert);

        alertConditionManagerRepository.deleteAllByAlertId(alertId);

        Set<String> indicators = request.conditions().stream()
                .map(AlertUpdateRequest.ConditionRequest::indicator)
                .collect(Collectors.toSet());

        List<AlertCondition> condList = alertConditionRepository.findByIndicatorIn(indicators);
        Map<String, AlertCondition> condMap = condList.stream()
                .collect(Collectors.toMap(AlertCondition::getIndicator, c -> c));

        List<AlertResponse.ConditionResponse> conditionResponses = new ArrayList<>();
        for (var c : request.conditions()) {
            AlertCondition cond = condMap.get(c.indicator());
            if (cond == null)
                throw new IllegalArgumentException("등록되지 않은 indicator: " + c.indicator());

            AlertConditionManager acm = AlertConditionManager.of(alert, cond, c.threshold(), c.threshold2());
            alertConditionManagerRepository.save(acm);

            conditionResponses.add(new AlertResponse.ConditionResponse(
                    cond.getId(),
                    cond.getIndicator(),
                    null,
                    c.threshold(),
                    c.threshold2(),
                    cond.getDescription()
            ));
        }
        // 조건 저장 이후 추가
        String indicatorsSummary = conditionResponses.stream()
                .map(c -> String.format("- %s: %.2f ~ %.2f", c.indicator(), c.threshold(), c.threshold2()))
                .collect(Collectors.joining("\n"));

        String aiFeedback;
        try {
//            aiFeedback = openAIService.getAIFeedback(indicatorsSummary);
        } catch (Exception e) {
            aiFeedback = alert.getAiFeedback(); // 기존 유지
        }

//        alert.setAiFeedback(aiFeedback);
        alertRepository.save(alert);


        return new AlertResponse(
                alert.getId(),
                alert.getStockCode(),
                alert.getTitle(),
                alert.getIsActived(),
                alert.getCreatedAt(),
                alert.getUpdatedAt(),
                conditionResponses,
                alert.getAiFeedback()
        );
    }

    @Transactional
    public void toggleAlert(Long userId, Long alertId, boolean isActived) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_NOT_FOUND));

        if (!alert.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        alert.setIsActived(isActived);
        alertRepository.save(alert);
    }

    public List<AlertResponse> triggerAlert(Long userId) {
        List<Alert> alertList = alertRepository.findByUserIdAndIsTriggered(userId, true);

        if (alertList.isEmpty()) return List.of();

        List<Long> alertIds = alertList.stream().map(Alert::getId).toList();

        List<AlertConditionManager> managers =
                alertConditionManagerRepository.findByAlertIdsWithCondition(alertIds);

        Map<Long, List<AlertResponse.ConditionResponse>> conditionMap = managers.stream()
                .collect(Collectors.groupingBy(
                        acm -> acm.getAlert().getId(),
                        Collectors.mapping(acm -> new AlertResponse.ConditionResponse(
                                acm.getAlertCondition().getId(),
                                acm.getAlertCondition().getIndicator(),
                                null,
                                acm.getThreshold(),
                                acm.getThreshold2(),
                                acm.getAlertCondition().getDescription()
                        ), Collectors.toList())
                ));

        return alertList.stream()
                .map(alert -> new AlertResponse(
                        alert.getId(),
                        alert.getStockCode(),
                        alert.getTitle(),
                        alert.getIsActived(),
                        alert.getCreatedAt(),
                        alert.getUpdatedAt(),
                        conditionMap.getOrDefault(alert.getId(), List.of()),
                        alert.getAiFeedback()
                ))
                .toList();
    }
}
