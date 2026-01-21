package com.example.backend.dto;

import com.example.backend.model.EmployeeEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String userName;
    private Boolean isActive;
    private EmployeeEntity employee;
}
