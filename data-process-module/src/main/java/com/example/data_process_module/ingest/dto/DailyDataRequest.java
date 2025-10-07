package com.example.data_process_module.ingest.dto;

import lombok.Data;

@Data
public class DailyDataRequest {
    private String symbol;
    private double prevClose;
    private long prevVolume;
    private double openPrice;
    private double high52w;
    private double low52w;
}
