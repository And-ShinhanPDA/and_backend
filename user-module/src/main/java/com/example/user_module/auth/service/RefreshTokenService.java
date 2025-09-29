package com.example.user_module.auth.service;

import com.example.user_module.common.security.jwt.RefreshTokenResponseDTO;

public interface RefreshTokenService {
    RefreshTokenResponseDTO refreshToken(String refreshToken);
}
