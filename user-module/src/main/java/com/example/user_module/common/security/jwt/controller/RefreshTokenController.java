package com.example.user_module.common.security.jwt.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.jwt.JwtProvider;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.dto.RefreshReq;
import com.example.user_module.common.security.jwt.service.RefreshTokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshReq request) {
        RefreshToken stored = refreshTokenService.validate(
                request.refreshTokenId(),
                request.refreshToken()
        );

        String newAccessToken = jwtProvider.generateAccessToken(stored.getUser().getId());

        return ResponseEntity.ok(ApiResponse.success(
                ResponseCode.SUCCESS_REISSUE,
                Map.of("accessToken", newAccessToken)
        ));
    }

}
