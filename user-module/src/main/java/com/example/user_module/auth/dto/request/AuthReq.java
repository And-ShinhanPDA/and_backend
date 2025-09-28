package com.example.user_module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public class AuthReq {
    public record signUpReq (
        @NotBlank
        String email,
        @NotBlank
        String name,
        @NotBlank
        String password
    ) { }

    public record loginReq (
            @NotBlank
            String email,

            @NotBlank
            String password
    ) { }
}