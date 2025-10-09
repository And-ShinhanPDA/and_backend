package com.example.user_module.common.security.jwt;

import com.example.user_module.auth.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private Key key;// Refresh Token 만료 (예: 7일)


    @PostConstruct
    public void init() {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isBlank()) {
            throw new IllegalStateException("❌ JWT_SECRET is null — check .env import in alert-module!");
        }
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }


    public String generateAccessToken(Long id) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getAccessExpirationTime()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long id) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getRefreshExpirationTime()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            return false; // 잘못된 토큰이면 false
        }
    }

    /**
     * 토큰에서 사용자 Email 추출
     */
    public String getId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰 만료 시간(ms) 반환
     */
    public Long getExpirationTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    /**
     * Claims 생성
     */
    private Claims getClaims(UserEntity user) {
        return Jwts.claims().setSubject(user.getEmail());
    }


    // refresh 토큰 만료 시간 반환
    public Long getRefreshExpirationTime() {
        return jwtProperties.getRefreshExpirationTime();
    }

    public String generateExpiredToken(Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now.getTime() - 10_000)) // 10초 전 발급
                .setExpiration(new Date(now.getTime() - 5_000)) // 5초 전에 만료
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
