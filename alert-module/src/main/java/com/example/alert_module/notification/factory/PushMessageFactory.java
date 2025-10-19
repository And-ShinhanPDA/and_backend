package com.example.alert_module.notification.factory;

import com.example.alert_module.notification.dto.PushMessage;
import org.springframework.stereotype.Component;

@Component
public class PushMessageFactory {

    public PushMessage createAlertCompany(String company, String alertTitle, boolean triggered, String categorySentence) {
        if (triggered) {
            return PushMessage.builder()
                    .title(String.format("📈[%s]%s 조건 충족!", company, alertTitle))
                    .body(String.format("%s 조건을 만족했습니다.", categorySentence))
                    .build();
        } else {
            return PushMessage.builder()
                    .title(String.format("📉[%s]%s 조건 해제!", company, alertTitle))
                    .body(String.format("%s 조건을 벗어났습니다.", categorySentence))
                    .build();
        }
    }

    public PushMessage createAlertCondition(String company, String alertTitle) {
        return PushMessage.builder()
                .title(String.format("📈%s 조건 충족!", alertTitle))
                .body(String.format("%s가 조건에 충족되었습니다.", company))
                .build();
    }

    public PushMessage createAlertPrice(String company, String price) {
        return PushMessage.builder()
                .title(String.format("[%s] 시가/종가 알림", company))
                .body(String.format("시작가/종가 %s원입니다.", price))
                .build();
    }

    public PushMessage test(String title, String body) {
        return PushMessage.builder()
                .title(String.format("%s", title))
                .body(String.format("%s", body))
                .build();
    }
}
