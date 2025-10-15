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
        try {
            String prompt = """
            너는 주식 투자 보조 AI야.
            조건으로 올 변수명이 의미하는거는 다음과 같아.
            PRICE_ABOVE	현재가가 목표가 이상일 때
            PRICE_BELOW	현재가가 목표가 이하일 때
            PRICE_CHANGE_DAILY_UP	현재가가 시가 대비 설정 금액 이상 상승할 때
            PRICE_CHANGE_DAILY_DOWN	현재가가 시가 대비 설정 금액 이상 하락할 때
            PRICE_CHANGE_BASE_UP	현재가가 기준 시점 대비 설정 금액 이상 상승할 때
            PRICE_CHANGE_BASE_DOWN	현재가가 기준 시점 대비 설정 금액 이상 하락할 때
            PRICE_RATE_DAILY_UP	현재가가 시가 대비 설정 백분율 이상일 때
            PRICE_RATE_DAILY_DOWN	현재가가 시가 대비 설정 백분율 이하일 때
            PRICE_RATE_BASE_UP	현재가가 기준 시점 대비 설정 백분율 이상일 때
            PRICE_RATE_BASE_DOWN	현재가가 기준 시점 대비 설정 백분율 이하일 때
            TRAILING_STOP_PRICE	현재가가 최근 고가 대비 설정 금액 이상 하락할 때 (추적 손절매)
            TRAILING_STOP_PERCENT	현재가가 최근 고가 대비 설정 비율 이상 하락할 때 (추적 손절매)
            TRAILING_BUY_PRICE	현재가가 최근 고가 대비 설정 금액 이상 상승할 때 (추적 매수)
            TRAILING_BUY_PERCENT	현재가가 최근 고가 대비 설정 비율 이상 상승할 때 (추적 매수)
            OPEN_PRICE	매일 시가를 알림
            CLOSE_PRICE	매일 종가를 알림
            HIGH_52W	현재가가 최근 52주 최고가 이상일 때
            LOW_52W	현재가가 최근 52주 최저가 이하일 때
            NEAR_HIGH_52W	현재가가 52주 최고가 기준 특정 % 이내 접근할 때
            NEAR_LOW_52W	현재가가 52주 최저가 기준 특정 % 이내 접근할 때
            VOLUME_AVG_DEV_UP	거래량이 평균 거래량 대비 설정 백분율 이상일 때
            VOLUME_AVG_DEV_DOWN	거래량이 평균 거래량 대비 설정 백분율 이하일 때
            VOLUME_CHANGE_PERCENT_UP	거래량이 전일 대비 설정 백분율 이상일 때
            VOLUME_CHANGE_PERCENT_DOWN	거래량이 전일 대비 설정 백분율 이하일 때
            SMA_5_UP	설정값이 5일 이동평균선 이상일 때
            SMA_5_DOWN	설정값이 5일 이동평균선 이하일 때
            SMA_10_UP	설정값이 10일 이동평균선 이상일 때
            SMA_10_DOWN	설정값이 10일 이동평균선 이하일 때
            SMA_20_UP	설정값이 20일 이동평균선 이상일 때
            SMA_20_DOWN	설정값이 20일 이동평균선 이하일 때
            SMA_30_UP	설정값이 30일 이동평균선 이상일 때
            SMA_30_DOWN	설정값이 30일 이동평균선 이하일 때
            SMA_50_UP	설정값이 50일 이동평균선 이상일 때
            SMA_50_DOWN	설정값이 50일 이동평균선 이하일 때
            SMA_100_UP	설정값이 100일 이동평균선 이상일 때
            SMA_100_DOWN	설정값이 100일 이동평균선 이하일 때
            SMA_200_UP	설정값이 200일 이동평균선 이상일 때
            SMA_200_DOWN	설정값이 200일 이동평균선 이하일 때
            RSI_OVER	RSI가 설정값 초과 (과매수)
            RSI_UNDER	RSI가 설정값 미만 (과매도)
            BOLLINGER_UPPER_TOUCH	현재가가 상단 볼린저 밴드 이상일 때 (강세 신호)
            BOLLINGER_LOWER_TOUCH	현재가가 상단 볼린저 밴드 이하일 때 (약세 신호)
            
            그리고 숫자 값들은 이 값 이하이거나 이상일때 이런의미야
            이 내용들을 참고해서 이 조건들의 조합이 충족돼서 알림이 울린다면 어떤의미일지 50자 이내의 피드백을 줘.
            조건:
            """ + indicatorsSummary;


            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a financial analyst who provides concise feedback on stock indicators."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + openAIApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // ✅ 응답 파싱
            if (response == null || !response.containsKey("choices"))
                return "AI 응답을 받을 수 없습니다.";

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices.isEmpty()) return "AI 응답이 비어 있습니다.";

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return message.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 피드백 생성 중 오류 발생: " + e.getMessage();
        }
    }
}