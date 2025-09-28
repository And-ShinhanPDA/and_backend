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

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message ) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
