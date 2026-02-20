package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchStockResponseDTO {
    private Long branchStockId;
    private Long branchId;
    private String branchName;
    private Long ingredientId;
    private String ingredientName;
    private String sku;
    private String unit;
    private Double currentStock;
    private Double reorderLevel;
    private Double costPerUnit;
}
