package com.example.data_process_module.persist.service;

import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.repository.DailyCandleRepository;
import java.time.Duration;
import java.util.Map;
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


    public void saveDaily(DailyCandleEntity entity) {
//        dailyRepo.save(entity);
        log.info("일별 데이터 저장됐다치자");
    }
}

