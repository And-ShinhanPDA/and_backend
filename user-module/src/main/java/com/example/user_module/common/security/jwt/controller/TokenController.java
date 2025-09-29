package com.example.user_module.common.security.jwt.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.jwt.dto.RefreshReq;
import com.example.user_module.common.security.jwt.service.RefreshTokenService;
import com.example.user_module.common.security.jwt.dto.RefreshRes;
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
    public ApiResponse<RefreshRes> refresh(@RequestBody RefreshReq.RefreshRequest request) {
        return ApiResponse.success(ResponseCode.SUCCESS_REISSUE,
                refreshTokenService.refreshToken(request.refreshToken()));
    }
}