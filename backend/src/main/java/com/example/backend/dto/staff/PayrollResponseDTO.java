package com.example.backend.dto.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponseDTO {
    private Long employeeId;
    private String fullName;
    private String position;
    private Double baseSalary;
    private Double hourlyRate;
    private Double totalHoursWorked;
    private Double hourlyEarnings;
    private Double totalEarnings;
    private Integer daysWorked;
    private Integer lateOccurrences;
    private Integer totalLateMinutes;
}
