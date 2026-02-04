package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecipeResponseDTO {
    private Long recipeId;
    private Long menuItemId;
    private Long ingredientId;
    private String ingredientName;
    private String ingredientUnit;
    private Double quantityNeeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
