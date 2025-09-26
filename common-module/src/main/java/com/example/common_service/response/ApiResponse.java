package com.example.common_service.response;

import lombok.Builder;
import lombok.Getter;


@Getter
public class ApiResponse<T> {

    private final String message;
    private final T data;
    private final ApiError error;

    @Builder
    private ApiResponse(String message, T data, ApiError error) {
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String detail) {
        return ApiResponse.<T>builder()
                .message(detail)
                .error(new ApiError(code, detail))
                .build();
    }
}
