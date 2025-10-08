package com.example.user_module.auth.controller;

import com.example.common_service.response.ApiResponse;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    @GetMapping("/me")
    public ApiResponse<?> getMyId(@AuthUser Long userId) {
        if (userId == null) {
            return ApiResponse.error(ResponseCode.UNAUTHORIZED);
        }
        return ApiResponse.success(ResponseCode.SUCCESS_LOGIN, userId);
    }

    @GetMapping("/profile")
    public String profile() {
        return "Hello JWT!";
    }
}
