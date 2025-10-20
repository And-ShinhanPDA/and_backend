package com.example.data_process_module.persist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DailyCandleResponse {

    private String stockCode;
    private LocalDateTime date;

    private Double openPrice;
    private Double closePrice;
    private Double highPrice;
    private Double lowPrice;
    private Integer volume;

    private Double rsi14;
    private Double bbUpper;
    private Double bbLower;
    private Double sma5;
    private Double sma10;
    private Double sma20;
    private Double sma30;
    private Double sma50;
    private Double sma100;
    private Double sma200;

    private Double diffFromPrev;
    private Double diffPerFromPrev;
}

