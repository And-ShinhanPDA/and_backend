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
    PRICE_RATE_DAILY_UP,
    PRICE_RATE_DAILY_DOWN,
    PRICE_RATE_BASE_UP,
    PRICE_RATE_BASE_DOWN,
    TRAILING_STOP_PRICE,
    TRAILING_BUY_PRICE,
    TRAILING_STOP_PERCENT,
    TRAILING_BUY_PERCENT,

    // daily_price
    OPEN_PRICE,
    CLOSE_PRICE,

    // rsi_alert
    RSI_OVER,
    RSI_UNDER,

    // bollinger_alert
    BOLLINGER_UPPER_TOUCH,
    BOLLINGER_LOWER_TOUCH,

    // fifty_two_week
    HIGH_52W,
    LOW_52W,
    NEAR_HIGH_52W,
    NEAR_LOW_52W,

    // volume_alert
    VOLUME_AVG_DEV_UP,
    VOLUME_AVG_DEV_DOWN;

}
