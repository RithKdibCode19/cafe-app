package com.example.backend.dto.payment;

import lombok.Data;

@Data
public class BakongKhqrRequestDTO {
    private String bakongAccountId;
    private String merchantName;
    private Double amount;
    private String currency; // "USD" or "KHR"
    private String billNumber;
}
