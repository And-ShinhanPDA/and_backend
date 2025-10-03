package com.example.data_process_module.transform.util;

import java.util.List;

public class CalculationUtil {

    public static double calculateSMA(List<Double> closes, int period) {
        if (closes.size() < period) return 0.0;
        return closes.subList(closes.size() - period, closes.size())
                .stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public static double calculateRSI(List<Double> closes, int period) {
        if (closes.size() <= period) return 0.0;
        double gain = 0, loss = 0;
        for (int i = closes.size() - period + 1; i < closes.size(); i++) {
            double diff = closes.get(i) - closes.get(i - 1);
            if (diff >= 0) gain += diff;
            else loss -= diff;
        }
        if (loss == 0) return 100.0;
        double rs = gain / loss;
        return 100 - (100 / (1 + rs));
    }

    public static double[] calculateBollinger(List<Double> closes, int period, double stdMultiplier) {
        if (closes.size() < period) return new double[]{0.0, 0.0};
        List<Double> periodCloses = closes.subList(closes.size() - period, closes.size());

        double mean = periodCloses.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = periodCloses.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0.0);
        double stddev = Math.sqrt(variance);

        return new double[]{mean + stdMultiplier * stddev, mean - stdMultiplier * stddev};
    }

    public static double calculateAverageVolume(List<Integer> volumes, int period) {
        if (volumes == null || volumes.size() < period) return 0.0;
        return volumes.subList(volumes.size() - period, volumes.size())
                .stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
