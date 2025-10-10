package com.example.common_service.exception;

import com.example.common_service.response.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlertException extends RuntimeException {
    private final ResponseCode responseCode;

    public AlertException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public HttpStatus getHttpStatus() {return responseCode.getHttpStatus();}
}
