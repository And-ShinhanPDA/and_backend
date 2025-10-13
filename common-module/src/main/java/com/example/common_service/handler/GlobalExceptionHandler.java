package com.example.common_service.handler;

import com.example.common_service.exception.AlertException;
import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        log.error("[INTERNAL_SERVER_ERROR]", e);
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

    @ExceptionHandler(AlertException.class)
    protected ResponseEntity<ApiResponse<?>> handleAlertException(AlertException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(e.getResponseCode()));
    }
}
