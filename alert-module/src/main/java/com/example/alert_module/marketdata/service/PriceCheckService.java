package com.example.alert_module.marketdata.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceCheckService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Map<String, Object>> fetchPrice(String stockCode) {
        String redisKey = "daily:" + stockCode;
        try {
            String json = redisTemplate.opsForValue().get(redisKey);
            if (json == null) return Optional.empty();

            Map<String, Object> data = objectMapper.readValue(json, new TypeReference<>() {});
            log.info("📊 [{}] 가격 데이터 로드: open={}, close={}", stockCode, data.get("openPrice"), data.get("closePrice"));
            return Optional.of(data);
        } catch (Exception e) {
            log.error("❌ Redis 가격 조회 실패 [{}]: {}", stockCode, e.getMessage());
            return Optional.empty();
        }
    }
}
