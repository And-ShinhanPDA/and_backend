package com.example.user_module.common.security.jwt.service;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken save(UserEntity user, String token, LocalDateTime expiryAt) {
        if (expiryAt == null) {
            expiryAt = LocalDateTime.now().plusDays(7); // ✅ fallback
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryAt(expiryAt) // ✅ null 방지
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public RefreshToken validate(UUID refreshTokenId) {
        RefreshToken stored = refreshTokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new AuthException(ResponseCode.REFRESH_TOKEN_NOT_FOUND));

        if (stored.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new AuthException(ResponseCode.EXPIRED_REFRESH_TOKEN);
        }

        return stored;
    }

    @Override
    public void delete(UUID refreshTokenId) {
        refreshTokenRepository.deleteById(refreshTokenId);
    }
}
