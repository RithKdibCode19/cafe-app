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

    // Manual Getters/Setters
    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(Double priceAdjustment) { this.priceAdjustment = priceAdjustment; }
}
