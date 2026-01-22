package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInResponseDTO {

    private Long stockId;
    private SupplierResponseDTO supplier;
    private IngredientResponseDTO ingredient;
    private Double qtyIn;
    private Double unitCost;
    private Double totalCost;
    private String invoiceNo;
    private LocalDateTime receivedDate;
    private Long receivedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
