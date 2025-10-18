package com.example.alert_module.management.service;

import com.example.alert_module.evaluation.entity.ConditionSearchResult;
import com.example.alert_module.evaluation.repository.ConditionSearchResultRepository;
import com.example.alert_module.management.dto.ConditionSearchResponse;
import com.example.alert_module.management.entity.AlertConditionManager;
import com.example.alert_module.management.repository.AlertConditionManagerRepository;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionSearchService {

    private final ConditionSearchResultRepository conditionSearchResultRepository;
    private final AlertConditionManagerRepository alertConditionManagerRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Map<String, String> GROUP_TO_SOURCE = Map.ofEntries(
            Map.entry("현재가", "minute"),
            Map.entry("거래량", "minute"),
            Map.entry("시가", "daily"),
            Map.entry("52주 최저가", "minute"),
            Map.entry("52주 최고가", "minute"),
            Map.entry("sma5", "daily"),
            Map.entry("sma10", "daily"),
            Map.entry("sma20", "daily"),
            Map.entry("sma30", "daily"),
            Map.entry("sma50", "daily"),
            Map.entry("sma100", "daily"),
            Map.entry("sma200", "daily"),
            Map.entry("rsi", "daily"),
            Map.entry("볼린저밴드", "daily"),
            Map.entry("전날 종가", "minute")
    );

    private static final Map<String, List<String>> GROUP_TO_FIELDS = Map.ofEntries(
            Map.entry("현재가", List.of("price")),
            Map.entry("거래량", List.of("volume")),
            Map.entry("시가", List.of("openPrice")),
            Map.entry("52주 최저가", List.of("lowPrice")),
            Map.entry("52주 최고가", List.of("highPrice")),
            Map.entry("sma5", List.of("sma5")),
            Map.entry("sma10", List.of("sma10")),
            Map.entry("sma20", List.of("sma20", "avgVol20")),
            Map.entry("sma30", List.of("sma30")),
            Map.entry("sma50", List.of("sma50")),
            Map.entry("sma100", List.of("sma100")),
            Map.entry("sma200", List.of("sma200")),
            Map.entry("rsi", List.of("rsi14")),
            Map.entry("볼린저밴드", List.of("bbUpper", "bbLower")),
            Map.entry("전날 종가", List.of("diffFromOpen", "diffFromOpenPct"))
    );

    private static final Map<String, String> INDICATOR_TO_GROUP = Map.ofEntries(
            Map.entry("PRICE_CHANGE_DAILY_UP", "시가"),
            Map.entry("PRICE_CHANGE_DAILY_DOWN", "시가"),
            Map.entry("PRICE_RATE_DAILY_UP", "시가"),
            Map.entry("PRICE_RATE_DAILY_DOWN", "시가"),

            Map.entry("LOW_52W", "52주 최저가"),
            Map.entry("NEAR_LOW_52W", "52주 최저가"),
            Map.entry("HIGH_52W", "52주 최고가"),
            Map.entry("NEAR_HIGH_52W", "52주 최고가"),

            Map.entry("SMA_5_UP", "sma5"),
            Map.entry("SMA_5_DOWN", "sma5"),
            Map.entry("SMA_10_UP", "sma10"),
            Map.entry("SMA_10_DOWN", "sma10"),
            Map.entry("SMA_20_UP", "sma20"),
            Map.entry("SMA_20_DOWN", "sma20"),
            Map.entry("VOLUME_AVG_DEV_UP", "sma20"),
            Map.entry("VOLUME_AVG_DEV_DOWN", "sma20"),
            Map.entry("SMA_30_UP", "sma30"),
            Map.entry("SMA_30_DOWN", "sma30"),
            Map.entry("SMA_50_UP", "sma50"),
            Map.entry("SMA_50_DOWN", "sma50"),
            Map.entry("GOLDEN_CROSS", "sma50"),
            Map.entry("DEAD_CROSS", "sma50"),
            Map.entry("SMA_100_UP", "sma100"),
            Map.entry("SMA_100_DOWN", "sma100"),
            Map.entry("SMA_200_UP", "sma200"),
            Map.entry("SMA_200_DOWN", "sma200"),

            Map.entry("RSI_OVER", "rsi"),
            Map.entry("RSI_UNDER", "rsi"),

            Map.entry("BOLLINGER_UPPER_TOUCH", "볼린저밴드"),
            Map.entry("BOLLINGER_LOWER_TOUCH", "볼린저밴드"),

            Map.entry("VOLUME_CHANGE_PERCENT_UP", "전날 종가"),
            Map.entry("VOLUME_CHANGE_PERCENT_DOWN", "전날 종가")
    );

    @Transactional(readOnly = true)
    public List<ConditionSearchResponse> getConditionSearchResults(Long alertId) {
        List<AlertConditionManager> managers = alertConditionManagerRepository.findByAlert_Id(alertId);
        List<ConditionSearchResult> triggeredResults =
                conditionSearchResultRepository.findByAlert_IdAndIsTriggeredTrue(alertId);

        if (triggeredResults.isEmpty() || managers.isEmpty()) {
            log.info("⚪ alertId={} 조건 탐지 없음", alertId);
            return List.of();
        }

        Set<String> activeGroups = managers.stream()
                .map(m -> INDICATOR_TO_GROUP.getOrDefault(m.getAlertCondition().getIndicator(), "현재가"))
                .collect(Collectors.toSet());
        activeGroups.add("현재가");
        activeGroups.add("거래량");

        log.info("🧭 [ConditionGroup] alertId={} → {}", alertId, activeGroups);

        List<ConditionSearchResponse> responses = triggeredResults.stream()
                .map(result -> {
                    String stockCode = result.getStockCode();
                    Map<String, Object> valueMap = getRedisValuesForGroups(stockCode, activeGroups);
                    return new ConditionSearchResponse(stockCode, result.getTriggerDate(), valueMap);
                })
                .toList();

        return responses;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getRedisValuesForGroups(String stockCode, Set<String> groups) {
        Map<String, Object> collected = new java.util.HashMap<>();

        for (String group : groups) {
            List<String> fields = GROUP_TO_FIELDS.getOrDefault(group, List.of("price"));
            String source = GROUP_TO_SOURCE.getOrDefault(group, "minute");
            String redisKey = source + ":" + stockCode;

            Map<String, Object> data = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
            if (data == null) continue;

            for (String field : fields) {
                Object val = data.get(field);
                collected.put(field, val);
            }
        }

        return collected;
    }

}
