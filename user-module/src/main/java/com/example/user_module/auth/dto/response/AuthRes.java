package com.example.user_module.auth.dto.response;

import java.util.UUID;

public class AuthRes {
    public record signUpRes(
            Long userId,
            String email,
            String name
    ) {}

    public record loginRes (
            Long userId,
            String email,
            String name,
            String accessToken,
            String refreshToken,
            UUID refreshTokenId
    ) {}
}
