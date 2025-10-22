package com.example.alert_module.notification.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Component
public class FcmInitializer {

    @Value("${fcm.firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            File file = new File(firebaseConfigPath);

            if (!file.exists()) {
                log.error("❌ Firebase config file not found at path: {}", file.getAbsolutePath());
                return;
            }

            try (InputStream stream = new FileInputStream(file)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(stream))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    log.info("✅ Firebase app initialized successfully using file: {}", file.getAbsolutePath());
                } else {
                    log.info("⚠️ Firebase app already initialized.");
                }
            }

        } catch (Exception e) {
            log.error("❌ Error initializing Firebase app", e);
        }
    }
}
