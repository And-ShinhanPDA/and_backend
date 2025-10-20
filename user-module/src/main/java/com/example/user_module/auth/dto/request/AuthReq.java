package com.example.user_module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public class AuthReq {
    public record signUpReq (
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,
        @NotBlank(message = "비밀번호 필수 입력값입니다.")
        String password,
        @NotBlank(message = "fcm 토큰이 전달되지 않았습니다.")
        String fcmToken,
        @NotBlank(message = "디바이스 id가 전달되지 않았습니다.")
        String deviceId
    ) { }

    public record loginReq (
            @NotBlank
            String email,

            @NotBlank
            String password,
            String deviceId
    ) { }

    public record logoutReq (
            String deviceId,
            UUID refreshTokenId
    ) {}
}