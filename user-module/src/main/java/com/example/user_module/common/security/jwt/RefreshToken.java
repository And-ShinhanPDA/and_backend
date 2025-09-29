package com.example.user_module.common.security.jwt;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ResponseCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * RefreshToken 저장 객체
 *
 * 운영 환경에서는 Redis 사용을 권장
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    // refreshToken → userId
    protected static final Map<String, Long> refreshTokens = new HashMap<>();

    /**
     * refresh token으로 userId 가져오기
     */
    public static Long getRefreshToken(final String refreshToken) {
        return Optional.ofNullable(refreshTokens.get(refreshToken))
                .orElseThrow(() -> new AuthException(ResponseCode.INVALID_REFRESH_TOKEN));
    }

    /**
     * refresh token 저장
     */
    public static void putRefreshToken(final String refreshToken, Long userId) {
        refreshTokens.put(refreshToken, userId);
    }

    /**
     * 특정 refresh token 삭제
     */
    private static void removeRefreshToken(final String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    /**
     * userId의 기존 refresh token 제거
     */
    public static void removeUserRefreshToken(final Long userId) {
        Iterator<Map.Entry<String, Long>> iterator = refreshTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getValue().equals(userId)) {
                iterator.remove();
            }
        }
    }
}
