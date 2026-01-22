package com.example.backend.dto.user;

import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.RoleEntity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotNull
    private String username;
    @Size(min=8, message = "Password must be at least 8 characters long")
    private String password;
    private EmployeeEntity employee;
    private RoleEntity role;
    private Boolean isActive;
}
