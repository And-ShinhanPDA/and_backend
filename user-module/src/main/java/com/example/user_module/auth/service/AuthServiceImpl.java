package com.example.user_module.auth.service;

import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.dto.response.AuthRes;
import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.auth.repository.UserRepository;
import com.example.user_module.common.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public AuthRes.signUpRes signUp(AuthReq.signUpReq signUpReq) {
        String encodedPassword = passwordEncoder.encode(signUpReq.password());

        UserEntity user = UserEntity.builder()
                .email(signUpReq.email())
                .password(encodedPassword)
                .name(signUpReq.name())
                .build();

        UserEntity saved = userRepository.save(user);

        return new AuthRes.signUpRes(saved.getId(), saved.getEmail(), saved.getName());
    }
}



