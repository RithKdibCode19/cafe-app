package com.example.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Check in time is required")
    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Integer lateMinute;

    @NotNull(message = "Status is required")
    private String status;

    private String note;
}
