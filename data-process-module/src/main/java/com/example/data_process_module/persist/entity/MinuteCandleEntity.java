package com.example.data_process_module.persist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "minuteCandle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MinuteCandleEntity.PK.class)
public class MinuteCandleEntity {

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private String stockCode;
        private LocalDateTime date;
    }
}
