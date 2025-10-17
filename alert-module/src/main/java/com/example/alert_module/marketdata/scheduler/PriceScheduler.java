package com.example.alert_module.marketdata.scheduler;

import com.example.alert_module.management.repository.AlertRepository;
import com.example.alert_module.marketdata.repository.PriceCheckRepository;
import com.example.alert_module.marketdata.service.PriceFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceScheduler {

    private final AlertRepository alertRepository;  // ✅ 변경
    private final PriceFetcher priceFetcher;

    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
    public void sendOpenPriceAlerts() {
        processPriceAlerts("openPrice");
    }

    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Seoul")
    public void sendClosePriceAlerts() {
        processPriceAlerts("closePrice");
    }

    private void processPriceAlerts(String priceType) {
        // ✅ 1️⃣ isPrice=true AND isActived=true 알림의 종목코드 조회
        List<String> stockCodes = alertRepository.findActivePriceAlertStockCodes();
        log.info("🔔 [{}] 알림 대상 종목 수: {}", priceType, stockCodes.size());

        // ✅ 2️⃣ 해당 종목들만 Redis에서 시가/종가 가져오기
        for (String code : stockCodes) {
            priceFetcher.fetchPrice(code).ifPresent(data -> {
                Double price = (Double) data.get(priceType);
                if (price != null) {
                    log.info("📩 [{}] {} = {}", priceType, code, price);
                    // TODO: 알림 전송 모듈 연동 예정
                }
            });
        }
    }
}