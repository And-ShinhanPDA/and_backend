package com.example.data_process_module.common.response;

import java.util.List;

public class ApiResponse<T> {

    private String message;
    private T data;
    private ApiError error;

    public ApiResponse() {}

    public ApiResponse(String message, T data, ApiError error) {
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, String type, List<String> details) {
        return new ApiResponse<>(message, null, new ApiError(type, details));
    }

    public String getMessage() { return message; }
    public T getData() { return data; }
    public ApiError getError() { return error; }
}
