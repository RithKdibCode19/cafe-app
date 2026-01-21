package com.example.backend.dto;



import com.example.backend.model.EmployeeEntity;

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
    private Boolean isActive;
}
