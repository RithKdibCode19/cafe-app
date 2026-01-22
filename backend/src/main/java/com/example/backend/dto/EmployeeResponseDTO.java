package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private Long employeeId;
    private String fullName;
    private String phone;
    private BranchResponseDTO branch;
    private String position;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
