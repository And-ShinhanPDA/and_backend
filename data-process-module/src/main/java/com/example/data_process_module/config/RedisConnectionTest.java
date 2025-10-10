package com.example.data_process_module.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisConnectionTest implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        try {
            redisTemplate.opsForValue().set("test:key", "hello redis");
            Object value = redisTemplate.opsForValue().get("test:key");
            System.out.println("✅ Redis 연결 성공: " + value);
        } catch (Exception e) {
            System.err.println("❌ Redis 연결 실패: " + e.getMessage());
        }
    }
}
