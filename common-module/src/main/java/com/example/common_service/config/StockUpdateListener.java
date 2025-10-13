package com.example.common_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockUpdateListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String stockCode = new String(message.getBody());
        log.info("📡 [SUBSCRIBE] Received stock update: {}", stockCode);
        // detectionService.detectForStock(stockCode); (다음 단계에서 연결)
    }
}

