package com.example.backend.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    
    private Long menuItemId;
    private String name;
    
    private Long categoryId;
    private String categoryName;
    
    private Double basePrice;
    private Boolean isActive;
    private String imageUrl;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
