package com.example.common_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public class ApiError {
    private String code;
    private String detail;

}
