package com.example.common_service.handler;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ApiResponse<?>> handleAuthException(AuthException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(e.getResponseCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        AuthException ex = new AuthException(ResponseCode.INVALID_INPUT);

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getResponseCode()));
    }
}
