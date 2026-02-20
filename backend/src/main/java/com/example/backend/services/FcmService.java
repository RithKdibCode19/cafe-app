package com.example.backend.services;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FcmService {

    @PostConstruct
    public void initialize() {
        try {
            ClassPathResource serviceAccount = new ClassPathResource("google-services.json");
            if (!serviceAccount.exists()) {
                log.warn("google-services.json not found in classpath. FCM notifications will be disabled.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase application", e);
        }
    }

    public void sendNotification(String token, String title, String body) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Cannot send notification: Firebase not initialized");
            return;
        }

        if (token == null || token.isEmpty()) {
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: " + response);
        } catch (Exception e) {
            log.error("Error sending FCM message to token: " + token, e);
        }
    }
}
