package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DrawerActionRequestDTO {
    private Long userId;
    private String type; // PAY_OUT, CASH_DROP, NO_SALE_OPEN
    private Double amount;
    private String reason;
}
