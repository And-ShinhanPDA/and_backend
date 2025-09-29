package com.example.user_module.common.security.jwt.service;

import com.example.user_module.common.security.jwt.dto.RefreshRes;

public interface RefreshTokenService {
    RefreshRes refreshToken(String refreshToken);
}
