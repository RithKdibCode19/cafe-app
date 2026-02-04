package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {

    private Long roleId;
    private String roleName;
    private String description;
    private java.util.List<PermissionResponseDTO> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
