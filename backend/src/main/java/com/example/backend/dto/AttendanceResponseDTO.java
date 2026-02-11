package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {

    private Long attendanceId;

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }
    private EmployeeResponseDTO employee;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer lateMinute;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
