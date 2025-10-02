package com.example.user_module.common.security.jwt.service;

import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.dto.RefreshRes;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken save(UserEntity user, String token, LocalDateTime expiryDate);

    RefreshToken validate(UUID refreshTokenId, String refreshTokenValue);

    void delete(UUID refreshTokenId);
}
