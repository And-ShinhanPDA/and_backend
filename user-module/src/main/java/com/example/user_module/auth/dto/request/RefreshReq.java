package com.example.user_module.auth.dto.request;

import lombok.Getter;

@Getter
public class RefreshReq {
    public record RefreshRequest(String refreshToken) {}
}
