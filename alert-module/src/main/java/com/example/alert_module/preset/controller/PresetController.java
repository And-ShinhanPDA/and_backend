package com.example.alert_module.preset.controller;

import com.example.alert_module.common.dto.ApiResponse;
import com.example.alert_module.preset.dto.PresetRequest;
import com.example.alert_module.preset.dto.PresetResponse;
import com.example.alert_module.preset.service.PresetService;
import com.example.user_module.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<ApiResponse<PresetResponse>> createPreset(
            @RequestBody PresetRequest request,
            @AuthUser Long userId
    ) {
        PresetResponse response = presetService.createPreset(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프리셋이 저장되었습니다.", response));
    }

    @DeleteMapping("/{presetId}")
    public ResponseEntity<ApiResponse<Void>> deletePreset(
            @AuthUser Long userId,
            @PathVariable Long presetId
    ) {
        presetService.deletePreset(userId, presetId);
        return ResponseEntity.ok(ApiResponse.success("프리셋 삭제 성공", null));
    }



}
