package com.example.alert_module.notification.factory;

import com.example.alert_module.notification.dto.PushMessage;
import org.springframework.stereotype.Component;

@Component
public class PushMessageFactory {

    public PushMessage createAlertCompany(String company, String alertTitle, boolean triggered, String categorySentence) {
        if (triggered) {
            return PushMessage.builder()
                    .title(String.format("🔔[%s]%s 조건 달성", company, alertTitle))
                    .body(String.format("%s 조건을 만족했습니다.", categorySentence))
                    .build();
        } else {
            return PushMessage.builder()
                    .title(String.format("🔕[%s]%s 조건 달성", company, alertTitle))
                    .body(String.format("%s 조건을 벗어났습니다.", categorySentence))
                    .build();
        }
    }

    public PushMessage createAlertCondition(String company, String alertTitle, String categorySentence) {
        return PushMessage.builder()
                .title(String.format("🔍[%s]%s 조건 달성", alertTitle, categorySentence))
                .body(String.format("%s(이)가 조건에 충족되었습니다.", company))
                .build();
    }

    public PushMessage createAlertPrice(String company, double price, String priceType) {
        String emoji = priceType.equals("시가") ? "🕘" : "🕞";
        String title = String.format("%s[%s]%s 알림", emoji, company, priceType);
        String body = String.format("%s의 %s는 %,.0f원입니다.", company, priceType, price);

        return PushMessage.builder()
                .title(title)
                .body(body)
                .build();
    }

    public PushMessage test(String title, String body) {
        return PushMessage.builder()
                .title(String.format("%s", title))
                .body(String.format("%s", body))
                .build();
    }
}
