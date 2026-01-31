package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long userId;
    private EmployeeResponseDTO employee;
    private String userName;
    private RoleResponseDTO role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
