package com.example.data_process_module.persist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MinuteCandleResponse {

    private String stockCode;
    private LocalDateTime date;

    private Double openPrice;
    private Double closePrice;
    private Double highPrice;
    private Double lowPrice;
    private Integer volume;

    private Double diffFromPrev;
}
