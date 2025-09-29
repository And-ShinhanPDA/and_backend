package com.example.user_module.auth.dto.response;

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
            String refreshToken
    ) {}
}
