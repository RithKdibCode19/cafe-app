package com.example.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInRequestDTO {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double qtyIn;

    @NotNull(message = "Unit cost is required")
    @Positive(message = "Unit cost must be positive")
    private Double unitCost;

    @NotNull(message = "Total cost is required")
    @Positive(message = "Total cost must be positive")
    private Double totalCost;

    private String invoiceNo;

    @NotNull(message = "Received date is required")
    private LocalDateTime receivedDate;

    @NotNull(message = "Received by is required")
    private Long receivedBy;
}
