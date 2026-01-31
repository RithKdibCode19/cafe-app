package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantRequestDTO {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price adjustment is required")
    private Double priceAdjustment;

}
