package com.example.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Payment method is required")
    private String method;

    @NotNull(message = "Paid amount is required")
    @Positive(message = "Paid amount must be positive")
    private Double paidAmount;

    private Double changeAmount;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    private LocalDateTime paidAt;

    private String transactionId;
}
