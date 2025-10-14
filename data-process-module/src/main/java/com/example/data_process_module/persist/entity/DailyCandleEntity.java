package com.example.data_process_module.persist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "dailyCandle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(DailyCandleEntity.PK.class)
public class DailyCandleEntity {
    @Id
    @Column(name = "stock_code", length = 50, nullable = false)
    private String stockCode;

    @Id
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "close_price")
    private Double closePrice;

    @Column(name = "high_price")
    private Double highPrice;

    @Column(name = "low_price")
    private Double lowPrice;

    @Column(name = "volume")
    private Integer volume;

    @Column(name = "rsi_14")
    private Double rsi14;

    @Column(name = "bb_upper")
    private Double bbUpper;

    @Column(name = "bb_lower")
    private Double bbLower;

    @Column(name = "sma_5")
    private Double sma5;

    @Column(name = "sma_10")
    private Double sma10;

    @Column(name = "sma_20")
    private Double sma20;

    @Column(name = "sma_30")
    private Double sma30;

    @Column(name = "sma_50")
    private Double sma50;

    @Column(name = "sma_100")
    private Double sma100;

    @Column(name = "sma_200")
    private Double sma200;

    @Transient
    private Double avgVol20;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String stockCode;
        private LocalDateTime date;
    }
}
