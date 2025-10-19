package com.example.alert_module.notification.controller;

import com.example.alert_module.notification.dto.PushMessage;
import com.example.alert_module.notification.factory.PushMessageFactory;
import com.example.alert_module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.el.util.MessageFactory;
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
    private final PushMessageFactory messageFactory;

    @PostMapping("/push")
    public ResponseEntity<String> sendPush(@RequestBody PushRequest request) {
        PushMessage pushMessage = messageFactory.test(request.title, request.body);
        notificationService.send(request.token(), pushMessage);
        return ResponseEntity.ok("푸시 전송 완료");
    }

    public record PushRequest(String token, String title, String body) {}
}
