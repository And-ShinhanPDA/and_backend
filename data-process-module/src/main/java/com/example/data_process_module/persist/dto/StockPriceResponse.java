package com.example.data_process_module.persist.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockPriceResponse {
    private String stockCode;
    private Double currentPrice;
    private Double prevClosePrice;
    private Double diff;
    private Double diffRate;
}
