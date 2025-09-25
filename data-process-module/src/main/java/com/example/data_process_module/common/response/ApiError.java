package com.example.data_process_module.common.response;

import java.util.List;

public class ApiError {

    private String type;
    private List<String> details;

    public ApiError() {}

    public ApiError(String type, List<String> details) {
        this.type = type;
        this.details = details;
    }

    public String getType() { return type; }
    public List<String> getDetails() { return details; }
}
