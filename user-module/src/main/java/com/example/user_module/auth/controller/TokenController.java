package com.example.user_module.auth.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.dto.request.RefreshReq;
import com.example.user_module.auth.service.RefreshTokenService;
import com.example.user_module.common.security.jwt.RefreshTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponseDTO> refresh(@RequestBody RefreshReq.RefreshRequest request) {
        return ApiResponse.success(ResponseCode.SUCCESS_REISSUE,
                refreshTokenService.refreshToken(request.refreshToken()));
    }
}