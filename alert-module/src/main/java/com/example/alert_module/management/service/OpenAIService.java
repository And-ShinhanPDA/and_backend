package com.example.alert_module.management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;


@Service
public class OpenAIService {

    private final WebClient webClient;

    public OpenAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    @Value("${openai.secret-key}")
    private String openAIApiKey;

    public String getAIFeedback(String indicatorsSummary) {
        String prompt = """
            너는 주식 투자 보조 AI야.
            사용자가 주식 알림 조건을 설정하고 있어.
            아래 조건들은 각기 다른 시그널(가격, 거래량, 추세 등)을 의미해.
            사용자가 선택한 조건 조합을 보고,
            이 알림이 어떤 투자 의미를 갖는 알림인지 1문장으로 설명해줘.
            즉, "이 알림은 어떤 상황을 감지하려는 목적의 알림인지"를 요약해야 해.
            
            설명은 아래처럼 짧고 직관적으로 써줘 (50자 이내):
            예시)
            - "단기 반등 시점을 포착하려는 알림입니다."
            - "과열 구간의 매도 신호를 탐지하는 조건입니다."
            - "거래량 증가와 추세 전환을 노린 매수 알림입니다."
            
            조건 설명:
            PRICE_ABOVE	현재가가 목표가 이상일 때
            PRICE_BELOW	현재가가 목표가 이하일 때
            PRICE_CHANGE_DAILY_UP	현재가가 시가 대비 설정 금액 이상 상승할 때
            PRICE_CHANGE_DAILY_DOWN	현재가가 시가 대비 설정 금액 이상 하락할 때
            PRICE_RATE_BASE_UP	현재가가 기준 시점 대비 설정 백분율 이상일 때
            PRICE_RATE_BASE_DOWN	현재가가 기준 시점 대비 설정 백분율 이하일 때
            TRAILING_STOP_PRICE	현재가가 최근 고가 대비 설정 금액 이상 하락할 때 (추적 손절매)
            TRAILING_BUY_PERCENT	현재가가 최근 고가 대비 설정 비율 이상 상승할 때 (추적 매수)
            HIGH_52W	현재가가 최근 52주 최고가 이상일 때
            LOW_52W	현재가가 최근 52주 최저가 이하일 때
            RSI_OVER	RSI가 설정값 초과 (과매수)
            RSI_UNDER	RSI가 설정값 미만 (과매도)
            BOLLINGER_UPPER_TOUCH	현재가가 상단 볼린저 밴드 이상일 때 (강세 신호)
            BOLLINGER_LOWER_TOUCH	현재가가 하단 볼린저 밴드 이하일 때 (약세 신호)
            SMA_20_UP	현재가가 20일 이동평균선 이상일 때
            SMA_20_DOWN	현재가가 20일 이동평균선 이하일 때
            ...
            
            이 조건 조합을 분석해서,
            이 알림이 어떤 시장상황(매수, 매도, 추세전환 등)을 감지하기 위한 것인지 설명해줘.
            
            조건:
            """ + indicatorsSummary;

        // ✅ 요청 본문
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a financial analyst who provides concise feedback on stock indicators."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        // ✅ 최대 2회 시도
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                Map<String, Object> response = webClient.post()
                        .uri("/chat/completions")
                        .header("Authorization", "Bearer " + openAIApiKey)
                        .header("Content-Type", "application/json")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response == null || !response.containsKey("choices"))
                    throw new RuntimeException("AI 응답을 받을 수 없습니다.");

                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (choices.isEmpty()) throw new RuntimeException("AI 응답이 비어 있습니다.");

                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString().replaceAll("^\"|\"$", "");

            } catch (Exception e) {
                System.err.println("⚠️ AI 피드백 생성 실패 (시도 " + attempt + "): " + e.getMessage());
                if (attempt == 2) {
                    // 2회 모두 실패 시 null 반환
                    return null;
                }
                try {
                    Thread.sleep(1000); // 1초 대기 후 재시도
                } catch (InterruptedException ignored) {}
            }
        }
        return null;
    }
}