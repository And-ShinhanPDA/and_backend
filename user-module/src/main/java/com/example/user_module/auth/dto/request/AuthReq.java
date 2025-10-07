package com.example.user_module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public class AuthReq {
    public record signUpReq (
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,
        @NotBlank(message = "비밀번호 필수 입력값입니다.")
        String password
    ) { }

    public record loginReq (
            @NotBlank
            String email,

            @NotBlank
            String password
    ) { }
}