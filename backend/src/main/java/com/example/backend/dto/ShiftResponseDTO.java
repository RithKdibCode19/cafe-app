package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponseDTO {

    private Long shiftId;
    private EmployeeResponseDTO employee;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;
    private BranchResponseDTO branch;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
