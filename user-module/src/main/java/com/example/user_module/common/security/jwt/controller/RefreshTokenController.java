package com.example.user_module.common.security.jwt.controller;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.AuthUser;
import com.example.user_module.common.security.jwt.JwtProvider;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.dto.RefreshReq;
import com.example.user_module.common.security.jwt.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshReq request,
                                     @CookieValue("refreshToken") String refreshToken) {

        // ✅ 서비스에 두 정보를 모두 넘겨 교차 검증
        Long userId = refreshTokenService.validateAndGetUserId(request.refreshTokenId(), refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(userId);

        return ResponseEntity.ok(ApiResponse.success(
                ResponseCode.SUCCESS_REISSUE,
                Map.of("accessToken", newAccessToken)
        ));
    }
}
