package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.model.VariantEntity.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponseDTO {
    private Long variantId;
    private Size size;
    private Double price;
}
