package com.example.data_process_module.transform.service;

import com.example.data_process_module.persist.entity.DailyCandleEntity;
import com.example.data_process_module.transform.util.CalculationUtil;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransformService {
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

        return entity;
    }
}