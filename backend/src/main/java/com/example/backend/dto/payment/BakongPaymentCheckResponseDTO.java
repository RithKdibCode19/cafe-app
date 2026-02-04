package com.example.backend.dto.payment;

import lombok.Data;

@Data
public class BakongPaymentCheckResponseDTO {
    private boolean success;
    private PaymentData data;

    @Data
    public static class PaymentData {
        private String status;
        private PaymentDetails payment;
    }

    @Data
    public static class PaymentDetails {
        private String hash;
        private String fromAccountId;
        private String toAccountId;
        private Double amount;
        private String currency;
        private String transactionId;
        private String time;
    }
}
