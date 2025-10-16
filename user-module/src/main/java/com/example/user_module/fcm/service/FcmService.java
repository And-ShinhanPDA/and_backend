package com.example.user_module.fcm.service;

import com.example.user_module.fcm.repository.FcmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmRepository fcmRepository;

    public void deactivateFcmToken(Long userId, String deviceId) {
        fcmRepository.deactivateToken(userId, deviceId);
    }
}