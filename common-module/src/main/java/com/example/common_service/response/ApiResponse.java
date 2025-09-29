package com.example.common_service.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String code;     // ex) SUCCESS_SIGN_UP, USER_NOT_FOUND
    private final String message;  // 응답 메시지
    private final T data;          // 성공 시 데이터, 실패 시 null

    @Builder
    private ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 성공 응답
    public static <T> ApiResponse<T> success(ResponseCode responseCode, T data) {
        return ApiResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(data)
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return ApiResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(null)
                .build();
    }
}
