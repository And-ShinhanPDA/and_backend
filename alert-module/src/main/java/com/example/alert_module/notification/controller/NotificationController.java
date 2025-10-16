package com.example.alert_module.notification.controller;

import com.example.alert_module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/push")
    public ResponseEntity<String> sendPush(@RequestBody PushRequest request) {
        notificationService.sendPush(request.token(), request.title(), request.body());
        return ResponseEntity.ok("푸시 전송 완료");
    }

    public record PushRequest(String token, String title, String body) {}
}
