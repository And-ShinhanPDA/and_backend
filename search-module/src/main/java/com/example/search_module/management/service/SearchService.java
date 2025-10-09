package com.example.search_module.management.service;

import com.example.search_module.management.dto.Request.SearchReq;
import com.example.search_module.management.dto.Response.AlertResponseDto;

public interface SearchService {
    AlertResponseDto createAlert(SearchReq.SearchAlertReq dto, Long userId);
}
