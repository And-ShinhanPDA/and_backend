package com.example.common_service.exception;

import com.example.common_service.response.ErrorCode;

public class AuthException extends RuntimeException{
    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // ErrorCode 안에 message 필드 정의했다고 가정
        this.errorCode = errorCode;
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}
