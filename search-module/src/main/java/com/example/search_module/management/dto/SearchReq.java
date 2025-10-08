package com.example.search_module.management.dto;

import java.util.List;

public class SearchReq {
    public record SearchAlertReq (
            String title,
            List<SearchAlertConditionReq> conditions
    ) {}


    public record SearchAlertConditionReq (
            String indicator,
            String operator,
            Double threshold,
            String description
    ) {}
}
