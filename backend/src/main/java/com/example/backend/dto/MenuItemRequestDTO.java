package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;
    private String nameKh;
    private String descriptionKh;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private Double basePrice;

    private String imageUrl;

    private Boolean isAvailable;

    // Manual Getters/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameKh() { return nameKh; }
    public void setNameKh(String nameKh) { this.nameKh = nameKh; }

    public String getDescriptionKh() { return descriptionKh; }
    public void setDescriptionKh(String descriptionKh) { this.descriptionKh = descriptionKh; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
