package com.example.data_process_module.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPublishService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publishStockUpdate(String stockCode) {
        String channel = "stock:update";
        redisTemplate.convertAndSend(channel, stockCode);
        log.info("📤 [PUBLISH] {} -> {}", channel, stockCode);
    }
}
