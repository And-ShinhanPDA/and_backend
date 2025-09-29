package com.example.common_service.exception;

import com.example.common_service.response.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException{
    private final ResponseCode responseCode;

    public AuthException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public HttpStatus getHttpStatus() {return responseCode.getHttpStatus();}
}
