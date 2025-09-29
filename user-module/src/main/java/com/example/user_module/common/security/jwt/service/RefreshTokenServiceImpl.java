package com.example.user_module.common.security.jwt.service;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.jwt.JwtProvider;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.dto.RefreshRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;

    @Override
    public RefreshRes refreshToken(final String refreshToken) {
        checkRefreshToken(refreshToken);

        // refresh token → userId 추출
        Long userId = RefreshToken.getRefreshToken(refreshToken);

        // 새 AccessToken 발급
        String newAccessToken = jwtProvider.generateAccessTokenByEmail(userId);

        // 기존 RefreshToken 제거
        RefreshToken.removeUserRefreshToken(userId);

        // 새 RefreshToken 발급
        String newRefreshToken = jwtProvider.generateRefreshTokenByEmail(userId);
        RefreshToken.putRefreshToken(newRefreshToken, userId);

        return RefreshRes.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    private void checkRefreshToken(final String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthException(ResponseCode.INVALID_REFRESH_TOKEN);
        }
    }
}
