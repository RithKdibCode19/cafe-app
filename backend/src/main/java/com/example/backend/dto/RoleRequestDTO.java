package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String description;
    private java.util.List<Long> permissionIds;

    public java.util.List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(java.util.List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }

    // Manual Getters/Setters
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
