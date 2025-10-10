package com.example.data_process_module.transform.service;

import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.persist.service.PersistService;
import com.example.data_process_module.transform.util.CalculationUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransformService {

    private final PersistService persistService;

    public DailyCandleEntity enrichWithIndicators(DailyCandleEntity entity,
                                                  List<Double> closePrices) {

        entity.setSma5(CalculationUtil.calculateSMA(closePrices, 5));
        entity.setSma10(CalculationUtil.calculateSMA(closePrices, 10));
        entity.setSma20(CalculationUtil.calculateSMA(closePrices, 20));
        entity.setSma30(CalculationUtil.calculateSMA(closePrices, 30));
        entity.setSma50(CalculationUtil.calculateSMA(closePrices, 50));
        entity.setSma100(CalculationUtil.calculateSMA(closePrices, 100));
        entity.setSma200(CalculationUtil.calculateSMA(closePrices, 200));

        entity.setRsi14(CalculationUtil.calculateRSI(closePrices, 14));

        double[] boll = CalculationUtil.calculateBollinger(closePrices, 20, 2);
        entity.setBbUpper(boll[0]);
        entity.setBbLower(boll[1]);

        log.info("[TRANSFORM] {} 지표 계산 완료", entity.getStockCode());

        persistService.saveDailyData(entity.getStockCode(), entity);

        return entity;
    }

    public Map<String, Double> calculateIntradayMetrics(
            double currentPrice,
            long currentVolume,
            double openPrice,
            double prevClose,
            long prevVolume,
            double high52w,
            double low52w
    ) {
        Map<String, Double> metrics = new HashMap<>();

        metrics.put("volumeRatio", prevVolume > 0 ? (currentVolume * 100.0 / prevVolume) : 0.0);
        metrics.put("diffFromOpen", currentPrice - openPrice);
        metrics.put("diffFromOpenPct", openPrice > 0 ? ((currentPrice - openPrice) / openPrice) * 100 : 0.0);
        metrics.put("diffFromHigh52wPct", high52w > 0 ? ((currentPrice - high52w) / high52w) * 100 : 0.0);
        metrics.put("diffFromLow52wPct", low52w > 0 ? ((currentPrice - low52w) / low52w) * 100 : 0.0);

        return metrics;
    }

}