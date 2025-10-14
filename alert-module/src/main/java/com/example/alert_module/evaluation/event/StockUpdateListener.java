package com.example.alert_module.evaluation.event;

import com.example.alert_module.evaluation.service.AlertDetectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockUpdateListener implements MessageListener {

    private final AlertDetectService alertDetectService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String stockCode = new String(message.getBody());
        log.info("📡 [SUBSCRIBE] Received stock update: {}", stockCode);
        alertDetectService.detectForStock(stockCode);
    }
}

