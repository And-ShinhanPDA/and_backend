package com.example.user_module.common.security.jwt.dto;

import lombok.Getter;

@Getter
public class RefreshReq {
    public record RefreshRequest(String refreshToken) {}
}
