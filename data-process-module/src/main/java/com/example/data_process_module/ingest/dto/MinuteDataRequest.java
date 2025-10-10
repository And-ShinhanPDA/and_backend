package com.example.data_process_module.ingest.dto;

import lombok.Data;

@Data
public class MinuteDataRequest {
    private String symbol;
    private double price;
    private long volume;
}
