package com.example.common_service.handler;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ApiResponse<?>> handleException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("500", e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ApiResponse<?>> handleAuthException(AuthException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }
}
