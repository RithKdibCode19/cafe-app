package com.example.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @Autowired
    private SystemSettingService settingService;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send a message to the configured Telegram chat
     */
    public boolean sendMessage(String message) {
        try {
            String botToken = settingService.getValue("TELEGRAM_BOT_TOKEN");
            String chatId = settingService.getValue("TELEGRAM_CHAT_ID");

            if (botToken == null || botToken.isEmpty() || chatId == null || chatId.isEmpty()) {
                logger.warn("Telegram not configured. Skipping notification.");
                return false;
            }

            String url = TELEGRAM_API_URL + botToken + "/sendMessage";

            Map<String, Object> payload = new HashMap<>();
            payload.put("chat_id", chatId);
            payload.put("text", message);
            payload.put("parse_mode", "HTML");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(url, request, String.class);

            logger.info("Telegram notification sent successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send Telegram message: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Test the Telegram connection
     */
    public boolean testConnection() {
        return sendMessage("🔔 <b>Test Notification</b>\n\nYour Cafe POS Telegram integration is working!");
    }

    /**
     * Send low stock alert
     */
    public void sendLowStockAlert(List<Map<String, Object>> lowStockItems) {
        if (!isAlertEnabled("NOTIFY_LOW_STOCK")) return;

        StringBuilder sb = new StringBuilder();
        sb.append("⚠️ <b>LOW STOCK ALERT</b>\n\n");
        sb.append("The following items are below reorder level:\n\n");

        for (Map<String, Object> item : lowStockItems) {
            sb.append("• <b>").append(item.get("name")).append("</b>\n");
            sb.append("   Current: ").append(item.get("currentStock")).append(" ").append(item.get("unit")).append("\n");
            sb.append("   Reorder at: ").append(item.get("reorderLevel")).append("\n\n");
        }

        sb.append("📦 Please restock soon.");
        sendMessage(sb.toString());
    }

    /**
     * Send large order notification
     */
    public void sendLargeOrderAlert(String orderNo, double total, int itemCount) {
        if (!isAlertEnabled("NOTIFY_LARGE_ORDER")) return;

        double threshold = getThreshold("LARGE_ORDER_THRESHOLD", 100.0);
        if (total < threshold) return;

        String message = String.format(
            "💰 <b>LARGE ORDER</b>\n\n" +
            "Order #%s\n" +
            "Total: <b>$%.2f</b>\n" +
            "Items: %d\n\n" +
            "🎉 Great sale!",
            orderNo, total, itemCount
        );

        sendMessage(message);
    }

    /**
     * Send shift discrepancy alert
     */
    public void sendShiftDiscrepancyAlert(String employeeName, double expected, double actual, double discrepancy) {
        if (!isAlertEnabled("NOTIFY_SHIFT_DISCREPANCY")) return;

        String emoji = discrepancy < 0 ? "🔴" : "🟡";
        String message = String.format(
            "%s <b>SHIFT DISCREPANCY</b>\n\n" +
            "Employee: %s\n" +
            "Expected Cash: $%.2f\n" +
            "Actual Cash: $%.2f\n" +
            "Discrepancy: <b>$%.2f</b>\n\n" +
            "⚠️ Please review.",
            emoji, employeeName, expected, actual, discrepancy
        );

        sendMessage(message);
    }

    /**
     * Send daily summary
     */
    public void sendDailySummary(double totalRevenue, int orderCount, double avgOrder) {
        String message = String.format(
            "📊 <b>DAILY SUMMARY</b>\n\n" +
            "Total Revenue: <b>$%.2f</b>\n" +
            "Orders: %d\n" +
            "Average Order: $%.2f\n\n" +
            "📈 Keep up the great work!",
            totalRevenue, orderCount, avgOrder
        );

        sendMessage(message);
    }

    // -- Helper Methods --

    private boolean isAlertEnabled(String key) {
        String value = settingService.getValue(key);
        return "true".equalsIgnoreCase(value);
    }

    private double getThreshold(String key, double defaultValue) {
        try {
            String value = settingService.getValue(key);
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
