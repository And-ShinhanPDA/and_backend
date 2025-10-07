package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.repository.DailyCandleRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistService {

    private final DailyCandleRepository dailyRepo;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveDailyData(String ticker, DailyCandleEntity entity) {
        String key = "daily:" + ticker;

        redisTemplate.opsForValue().set(key, entity, Duration.ofHours(24));

        log.info("[REDIS SAVE] 일별 데이터 {} -> {}", key, entity);
    }

    public void saveMinuteData(String ticker, Map<String, Double> metrics) {
        String key = "minute:" + ticker;

        redisTemplate.opsForValue().set(key, metrics, Duration.ofHours(2));

        log.info("[REDIS SAVE] 분별 데이터 {} -> {}", key, metrics);
    }

    public void syncDailyDataToDB() {
        Set<String> keys = redisTemplate.keys("daily:*");
        log.info("🔍 Redis에서 찾은 daily:* 키 목록 = {}", keys);

        if (keys == null || keys.isEmpty()) {
            log.warn("⚠️ Redis에 daily:* 키가 없습니다. (keys() 결과 비어있음)");
            return;
        }

        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.warn("⚠️ {} -> Redis에서 읽은 value가 null", key);
                continue;
            }

            log.info("🔍 {} 타입 = {}", key, value.getClass().getName());

            if (value instanceof DailyCandleEntity entity) {
                dailyRepo.save(entity);
                log.info("[SYNC] {} -> DB 저장 완료 (Entity)", key);
            } else if (value instanceof Map<?, ?> mapValue) {
                log.info("[SYNC] {} -> Map 구조로 저장되어 있음 (LinkedHashMap)", key);
                DailyCandleEntity entity = convertMapToEntity(mapValue);
                dailyRepo.save(entity);
                log.info("[SYNC] {} -> DB 저장 완료 (Map → Entity)", key);
            } else {
                log.warn("⚠️ {} -> 예기치 못한 타입: {}", key, value.getClass().getName());
            }
        }
    }

    private DailyCandleEntity convertMapToEntity(Map<?, ?> map) {
        DailyCandleEntity entity = new DailyCandleEntity();
        entity.setStockCode((String) map.get("stockCode"));
        entity.setDate(LocalDateTime.parse((String) map.get("date")));
        entity.setOpenPrice(getDouble(map.get("openPrice")));
        entity.setClosePrice(getDouble(map.get("closePrice")));
        entity.setHighPrice(getDouble(map.get("highPrice")));
        entity.setLowPrice(getDouble(map.get("lowPrice")));
        entity.setVolume(getInt(map.get("volume")));
        entity.setRsi14(getDouble(map.get("rsi14")));
        entity.setSma20(getDouble(map.get("sma20")));
        entity.setBbUpper(getDouble(map.get("bbUpper")));
        entity.setBbLower(getDouble(map.get("bbLower")));
        return entity;
    }

    private Double getDouble(Object obj) {
        return obj == null ? null : ((Number) obj).doubleValue();
    }

    private Integer getInt(Object obj) {
        return obj == null ? null : ((Number) obj).intValue();
    }




    public void saveDaily(DailyCandleEntity entity) {
//        dailyRepo.save(entity);
        log.info("일별 데이터 저장됐다치자");
    }
}

