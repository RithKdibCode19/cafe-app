package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentResponseDTO {

    private Long adjustmentId;

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }
    private IngredientResponseDTO ingredient;
    private Double qtyChange;
    private String reasonType;
    private String note;
    private String approvedByName;
    private String createdByName;
    private String status;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
