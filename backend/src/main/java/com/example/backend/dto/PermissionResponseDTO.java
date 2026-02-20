package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {

    private Long permissionId;
    private String code;
    private String permissionName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
