package com.example.backend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long paymentId;
    private Long orderId;
    private String orderNo;
    
    private String paymentMethod;
    private Double amount;
    private Double cashReceived;
    private Double changeAmount;
    
    private String transactionReference;
    private String status; // "COMPLETED", "PENDING", "FAILED"
    
    private String note;
    
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
}
