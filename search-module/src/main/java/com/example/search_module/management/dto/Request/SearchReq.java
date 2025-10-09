package com.example.search_module.management.dto.Request;

import java.util.List;

public class SearchReq {
    public record SearchAlertReq (
            String title,
            List<SearchConditionRequest> conditions
    ) {}

    public record SearchConditionRequest(
            String indicator,
            Double threshold,
            Double threshold2
    ) {}
}
