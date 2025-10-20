package com.example.data_process_module.ingest.controller;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisDebugController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/get")
    public ResponseEntity<String> getValue(@RequestParam String key) {
        String value = redisTemplate.opsForValue().get(key);
        return ResponseEntity.ok(value != null ? value : "❌ Key not found");
    }

    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getKeys() {
        Set<String> keys = redisTemplate.keys("*");
        return ResponseEntity.ok(keys);
    }
}

