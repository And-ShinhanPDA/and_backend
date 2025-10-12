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
    private final JwtProvider jwtProvider;;

    private static final String PREFIX = "refresh:user:";

    @Override
    public UUID save(UserEntity user, String token, LocalDateTime expiryAt) {
        UUID tokenId = UUID.randomUUID();
        String key = PREFIX + user.getId() + ":" + tokenId;

        redisTemplate.opsForValue().set(key, token, jwtProvider.getRefreshExpirationTime(), TimeUnit.SECONDS);

        return  tokenId;
    }

    @Override
    public Long validateAndGetUserId(UUID refreshTokenId, String refreshTokenFromCookie) {
        Set<String> keys = redisTemplate.keys(PREFIX + "*:" + refreshTokenId);
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

        String[] parts = key.split(":");
        Long userId = Long.parseLong(parts[2]);
        return userId;
    }

    @Override
    public void delete(UUID refreshTokenId) {
        Set<String> keys = redisTemplate.keys("refresh:user:*:" + refreshTokenId);
        if (keys != null) redisTemplate.delete(keys);
    }

}
