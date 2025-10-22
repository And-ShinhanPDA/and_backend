package com.example.user_module.auth.service;

import com.example.common_service.exception.AuthException;
import com.example.common_service.response.ResponseCode;
import com.example.user_module.auth.dto.request.AuthReq;
import com.example.user_module.auth.dto.response.AuthRes;
import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.auth.repository.UserRepository;
import com.example.user_module.common.security.jwt.JwtProvider;
import com.example.user_module.common.security.jwt.domain.RefreshToken;
import com.example.user_module.common.security.jwt.service.RefreshTokenService;
import com.example.user_module.fcm.entity.FcmToken;
import com.example.user_module.fcm.repository.FcmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final FcmRepository fcmRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthRes.signUpRes signUp(AuthReq.signUpReq signUpReq) {
        String encodedPassword = passwordEncoder.encode(signUpReq.password());

        if (userRepository.findByEmail(signUpReq.email()).isPresent()) {
            throw new AuthException(ResponseCode.USER_EXIST);
        }

        UserEntity user = UserEntity.builder()
                .email(signUpReq.email())
                .password(encodedPassword)
                .name(signUpReq.name())
                .build();

        UserEntity saved = userRepository.save(user);

        return new AuthRes.signUpRes(saved.getId(), saved.getEmail(), saved.getName());
    }

    @Override
    @Transactional
    public AuthRes.loginRes login(AuthReq.loginReq loginReq) {
        UserEntity user = userRepository.findByEmail(loginReq.email())
                .orElseThrow(() -> new AuthException(ResponseCode.LOGIN_FAIL));

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw new AuthException(ResponseCode.LOGIN_FAIL);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        RefreshToken refresh = refreshTokenService.save(
                user,
                refreshToken,
                LocalDateTime.now().plusDays(7)
        );

        // ✅ 이미 등록된 FCM 토큰 있는지 확인
        boolean exists = fcmRepository.findByUserIdAndDeviceId(user.getId(), loginReq.deviceId()).isPresent();

        if (!exists) {
            fcmRepository.save(FcmToken.builder()
                    .user(user)
                    .deviceId(loginReq.deviceId())
                    .fcmToken(loginReq.fcmToken())
                    .actived(true)
                    .build());

            log.info("✅ FCM 토큰 신규 등록: userId={}, deviceId={}", user.getId(), loginReq.deviceId());
        } else {
            fcmRepository.activateToken(user.getId(), loginReq.deviceId());
            log.info("⚠️ 이미 존재하는 FCM 토큰, 저장 스킵: userId={}, deviceId={}", user.getId(), loginReq.deviceId());
        }

        return new AuthRes.loginRes(
                user.getId(),
                user.getEmail(),
                user.getName(),
                accessToken,
                refreshToken,
                refresh.getId()
        );
    }

}
