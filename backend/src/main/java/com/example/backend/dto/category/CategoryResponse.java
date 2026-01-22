package com.example.backend.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    
    private Long categoryId;
    private String name;
    private String description;
    private Integer itemCount; // Number of menu items in this category
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
