package com.example.alert_module.management.service;

import com.example.alert_module.common.exception.CustomException;
import com.example.alert_module.common.exception.ErrorCode;
import com.example.alert_module.evaluation.entity.ConditionSearch;
import com.example.alert_module.evaluation.entity.ConditionSearchResult;
import com.example.alert_module.evaluation.repository.ConditionSearchRepository;
import com.example.alert_module.evaluation.repository.ConditionSearchResultRepository;
import com.example.alert_module.management.dto.*;
import com.example.alert_module.management.repository.*;
import com.example.alert_module.management.entity.*;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;
    private final OpenAIService openAIService;
    private final RedisTemplate<String, Object> redisTemplate;
//    private final ConditionSearchRepository conditionSearchRepository;
    private final ConditionSearchResultRepository conditionSearchResultRepository;

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
                                null,
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

            Double threshold2 = null;
            if (isBasePriceIndicator(c.indicator()) && request.stockCode() != null) {
                threshold2 = fetchCurrentPriceFromRedis(request.stockCode());
            }

            AlertConditionManager acm = new AlertConditionManager();
            acm.setAlert(alert);
            acm.setAlertCondition(cond);
            acm.setThreshold(c.threshold());
            acm.setThreshold2(threshold2);
            alertConditionManagerRepository.save(acm);

            conditionResponses.add(
                    new AlertResponse.ConditionResponse(
                            cond.getId(),
                            cond.getIndicator(),
                            null,
                            c.threshold(),
                            threshold2,
                            cond.getDescription()
                    )
            );
        }
        // ✅ AI 피드백 로직 추가
        String indicatorsSummary = conditionResponses.stream()
                .map(c -> String.format("- %s: %.2f ~ %.2f", c.indicator(), c.threshold(), c.threshold2()))
                .collect(Collectors.joining("\n"));

         //OpenAI 호출
        String aiFeedback = openAIService.getAIFeedback(indicatorsSummary);

        // ✅ 2. DB에도 aiFeedback 저장
        alert.setAiFeedback(aiFeedback);
        alertRepository.save(alert);

        List<String> stockCodes = List.of(
                "005930",  // 삼성전자
                "000660",  // SK하이닉스
                "373220",  // LG에너지솔루션
                "012450",  // 한화에어로스페이스
                "005380",  // 현대차
                "105560",  // KB금융
                "035420",  // NAVER
                "329180",  // HD현대중공업
                "068270",  // 셀트리온
                "034020",  // 두산에너빌리티
                "000270",  // 기아
                "055550",  // 신한지주
                "035720",  // 카카오
                "086790",  // 하나금융지주
                "015760",  // 한국전력
                "005490",  // POSCO홀딩스
                "011200",  // HMM
                "138040",  // 메리츠금융지주
                "316140",  // 우리금융지주
                "010130"   // 고려아연
        );

        if (request.stockCode() == null) {
            alert.setIsConditionSearch(true);
            for (String code : stockCodes) {
                ConditionSearchResult conditionSearch = ConditionSearchResult.builder()
                        .alert(alert)
                        .stockCode(code)
                        .isTriggered(false)
                        .triggerDate(null)
                        .build();
                conditionSearchResultRepository.save(conditionSearch);
            }
            log.info("🧩 조건 탐색용 알림 등록됨: alertId={}, {}개 종목 ConditionSearch 생성", alert.getId(), stockCodes.size());
        }

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
        alert.setIsTriggered(false);

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
        log.info("");
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_NOT_FOUND));

        if (!alert.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        alert.setIsActived(isActived);
        alertRepository.save(alert);
    }

    public List<AlertResponse> triggerAlert(Long userId) {
        List<Alert> alertList = alertRepository.findByUserIdAndIsTriggeredAndIsActivedTrue(userId, true);

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

    private boolean isBasePriceIndicator(String indicator) {
        return switch (indicator) {
            case "PRICE_CHANGE_BASE_UP",
                 "PRICE_CHANGE_BASE_DOWN",
                 "PRICE_RATE_BASE_UP",
                 "PRICE_RATE_BASE_DOWN",
                 "TRAILING_STOP_PRICE",
                 "TRAILING_STOP_PERCENT",
                 "TRAILING_BUY_PRICE",
                 "TRAILING_BUY_PERCENT"
                 -> true;
            default -> false;
        };
    }

    @SuppressWarnings("unchecked")
    private Double fetchCurrentPriceFromRedis(String stockCode) {
        try {
            Map<String, Object> minute = (Map<String, Object>) redisTemplate.opsForValue().get("minute:" + stockCode);
            if (minute == null || minute.get("price") == null) {
                throw new IllegalStateException("Redis에서 현재가를 찾을 수 없습니다: " + stockCode);
            }
            return Double.parseDouble(minute.get("price").toString());
        } catch (Exception e) {
            throw new IllegalStateException("현재가 조회 중 오류 발생: " + e.getMessage());
        }
    }

}
