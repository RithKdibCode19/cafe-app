package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    private Long orderItemId;
    private MenuItemResponseDTO menuItem;
    private VariantResponseDTO variant;
    private Integer qty;
    private Double unitPrice;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
