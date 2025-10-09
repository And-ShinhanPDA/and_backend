package com.example.search_module.management.controller;


import com.example.search_module.management.dto.Request.SearchReq;
import com.example.search_module.management.dto.Response.AlertResponseDto;
import com.example.search_module.management.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/searches")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<AlertResponseDto> createAlert(@Valid @RequestBody SearchReq dto) {
        Long userId = 1L; // JWT에서 가져올 예정
        AlertResponseDto response = searchService.createAlert(dto, userId);
        return ResponseEntity.ok(response);
    }
}