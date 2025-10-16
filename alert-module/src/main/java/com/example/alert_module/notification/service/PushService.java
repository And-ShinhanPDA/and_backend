package com.example.alert_module.notification.service;

import com.example.alert_module.notification.dto.AlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushService {

    public void send(AlertEvent event) {
        String title = "📈 조건 충족 알림";
        String body = String.format("%s 종목이 %s 조건을 만족했습니다.",
                event.stockCode(), event.title());
        log.info("🔔 [Push] userId={}, title={}, body={}",
                event.userId(), title, body);
        // TODO: 실제 FCM 전송 or WebSocket 메시지 로직 추가
    }
}