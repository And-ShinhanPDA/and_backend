package com.example.common_service.exception;

import com.example.common_service.response.ResponseCode;

public class AuthException extends RuntimeException{
    private final ResponseCode responseCode;

    public AuthException(ResponseCode responseCode) {
        super(responseCode.getMessage()); // ErrorCode 안에 message 필드 정의했다고 가정
        this.responseCode = responseCode;
    }

    public AuthException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.responseCode = responseCode;
    }

    public String getErrorCode() {
        return responseCode.getCode();
    }
}
