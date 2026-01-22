package com.example.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequestDTO {

    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    @NotNull(message = "Quantity change is required")
    private Double qtyChange;

    @NotBlank(message = "Reason type is required")
    private String reasonType;

    private String note;

    private Long approvedBy;

    @NotNull(message = "Created by is required")
    private Long createdBy;

    @NotNull(message = "Date is required")
    private LocalDateTime date;
}
