package com.example.data_process_module.persist.service;


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

    public Double getCurrentPrice(String stockCode) {
        String key = PRICE_KEY_PREFIX + stockCode;

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Map<?, ?> cachedMap) {
            Object priceObj = cachedMap.get("price");
            if (priceObj != null) {
                log.info("✅ Redis에서 price 필드 조회 성공 {} = {}", stockCode, priceObj);
                return d(priceObj);
            }
        }

        log.info("⚠️ 현재값 Redis miss → fetching from DB for {}", stockCode);
        Optional<Double> latestPrice = minuteCandleRepository.findLatestClosePriceByStockCode(stockCode);

        Double price = latestPrice.orElseThrow(
                () -> new IllegalArgumentException("해당 종목의 가격 정보를 찾을 수 없습니다.")
        );

        return price;
    }

    private Double d(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }
}
