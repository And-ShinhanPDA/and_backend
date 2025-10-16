package com.example.alert_module.management.dto;

public record GetCompanyRes(String stockCode, String name, Long alertCount, boolean isToggle) {
}
