package com.example.user_module.auth.service;

import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.dto.response.AuthRes;

public interface AuthService {

    AuthRes.signUpRes signUp(AuthReq.signUpReq signUpReq);

    AuthRes.loginRes login(AuthReq.loginReq loginReq);
}
