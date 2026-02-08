package com.example.backend.controller;

import com.example.backend.dto.common.ApiResponse;
import com.example.backend.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private TelegramService telegramService;

    /**
     * Test the Telegram connection
     */
    @PostMapping("/test")
    public ResponseEntity<?> testNotification() {
        boolean success = telegramService.testConnection();
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "Test notification sent successfully!"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send notification. Check your Telegram configuration."));
        }
    }

    /**
     * Manually trigger a notification (for testing)
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendCustomNotification(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Message is required"));
        }

        boolean success = telegramService.sendMessage(message);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "Notification sent!"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send notification."));
        }
    }
}
