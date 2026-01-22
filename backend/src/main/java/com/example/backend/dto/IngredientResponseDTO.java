package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponseDTO {

    private Long ingredientId;
    private String name;
    private String sku;
    private String unit;
    private Double reorderLevel;
    private Double currentStock;
    private Double costPerUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
