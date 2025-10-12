package com.example.alert_module.evaluation.evaluator;

public enum ConditionType {
    // sma_alert
    SMA_5_UP,
    SMA_10_UP,
    SMA_20_UP,
    SMA_30_UP,
    SMA_50_UP,
    SMA_100_UP,
    SMA_200_UP,
    SMA_5_DOWN,
    SMA_10_DOWN,
    SMA_20_DOWN,
    SMA_30_DOWN,
    SMA_50_DOWN,
    SMA_100_DOWN,
    SMA_200_DOWN,

    // price
    PRICE_ABOVE,
    PRICE_BELOW,
    PRICE_CHANGE_DAILY_UP,
    PRICE_CHANGE_DAILY_DOWN,
    PRICE_CHANGE_BASE_UP,
    PRICE_CHANGE_BASE_DOWN,


}
