package com.example.data_process_module.persist.service;


import com.example.data_process_module.persist.dto.StockPriceResponse;
import com.example.data_process_module.persist.repository.MinuteCandleRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockPriceService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MinuteCandleRepository minuteCandleRepository;

    private static final String PRICE_KEY_PREFIX = "minute:";
    private static final String DAILY_KEY_PREFIX = "daily:";

    @SuppressWarnings("unchecked")
    public StockPriceResponse getCurrentPrice(String stockCode) {
        Map<String, Object> minuteData = (Map<String, Object>) redisTemplate.opsForValue().get(PRICE_KEY_PREFIX + stockCode);
        Double currentPrice = d(minuteData != null ? minuteData.get("price") : null);

        Map<String, Object> dailyData = (Map<String, Object>) redisTemplate.opsForValue().get(DAILY_KEY_PREFIX + stockCode);
        Double prevClosePrice = d(dailyData != null ? dailyData.get("closePrice") : null);

        if (currentPrice == null) {
            currentPrice = minuteCandleRepository.findLatestClosePriceByStockCode(stockCode)
                    .orElseThrow(() -> new IllegalArgumentException("현재가 정보를 찾을 수 없습니다."));
        }

        Double diff = null;
        Double diffRate = null;
        if (prevClosePrice != null && prevClosePrice != 0) {
            diff = currentPrice - prevClosePrice;
            diffRate = (diff / prevClosePrice) * 100;
        }

        return StockPriceResponse.builder()
                .stockCode(stockCode)
                .currentPrice(currentPrice)
                .prevClosePrice(prevClosePrice)
                .diff(diff)
                .diffRate(diffRate)
                .build();
    }

    private Double d(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }
}
