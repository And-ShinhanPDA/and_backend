package com.example.user_module.common.security.jwt.service;

import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.dto.RefreshReq;
import com.example.user_module.common.security.jwt.dto.RefreshRes;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RefreshTokenService {
    UUID save(UserEntity user, String token, LocalDateTime expiryDate);

    Long validateAndGetUserId(UUID refreshTokenId, String refreshTokenFromCookie);

    void delete(UUID refreshTokenId);
}
