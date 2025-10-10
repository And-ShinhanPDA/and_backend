package com.example.alert_module.preset.controller;

import com.example.alert_module.preset.dto.PresetRequest;
import com.example.alert_module.preset.service.PresetService;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/presets")
@RequiredArgsConstructor
public class PresetController {
    private final PresetService presetService;

    @PostMapping
    public ResponseEntity<Long> createPreset(@RequestBody PresetRequest request, @AuthUser Long userId) {
        Long presetId = presetService.createPreset(userId, request); // 지금은 프리셋만 저장
        return ResponseEntity.ok(presetId);
    }

}
