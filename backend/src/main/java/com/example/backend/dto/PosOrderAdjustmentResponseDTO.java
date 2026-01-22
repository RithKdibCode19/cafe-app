package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderAdjustmentResponseDTO {

    private Long posOrderAdjustmentId;
    private Long orderId;
    private String type;
    private Double amount;
    private String reason;
    private Long requestedBy;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
