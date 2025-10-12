package com.example.user_module.common.security.jwt.service;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.common.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Primary
public class RefreshTokenRedisImpl implements RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    // refresh token TTL (7일)
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60L;

    /** ✅ Redis Key 생성 규칙 */
    private String getKey(Long userId, UUID refreshTokenId) {
        return "refresh:user:" + userId + ":" + refreshTokenId;
    }

    /** ✅ RefreshToken 저장 */
    @Override
    public UUID save(UserEntity user, String token, LocalDateTime expiryAt) {
        UUID tokenId = UUID.randomUUID();
        String key = getKey(user.getId(), tokenId);

        redisTemplate.opsForValue().set(key, token, REFRESH_TOKEN_EXPIRATION, TimeUnit.SECONDS);

        // 기존 AuthServiceImpl 호환을 위해 UUID 리턴
        return  tokenId;
    }

    /** ✅ 검증 및 userId 반환 */
    @Override
    public Long validateAndGetUserId(UUID refreshTokenId, String refreshTokenFromCookie) {
        // Redis 키 검색
        Set<String> keys = redisTemplate.keys("refresh:user:*:" + refreshTokenId);
        if (keys == null || keys.isEmpty()) {
            throw new AuthException(ResponseCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String key = keys.iterator().next();
        String storedToken = (String) redisTemplate.opsForValue().get(key);

        if (storedToken == null) {
            throw new AuthException(ResponseCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!storedToken.equals(refreshTokenFromCookie)) {
            throw new AuthException(ResponseCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(refreshTokenFromCookie)) {
            throw new AuthException(ResponseCode.INVALID_REFRESH_TOKEN);
        }

        // 키에서 userId 추출
        String[] parts = key.split(":");
        Long userId = Long.parseLong(parts[2]);
        return userId;
    }

    /** ✅ 개별 로그아웃 */
    @Override
    public void delete(UUID refreshTokenId) {
        Set<String> keys = redisTemplate.keys("refresh:user:*:" + refreshTokenId);
        if (keys != null) redisTemplate.delete(keys);
    }

}
