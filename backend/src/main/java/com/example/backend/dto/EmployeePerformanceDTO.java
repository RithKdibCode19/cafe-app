package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePerformanceDTO {
    private Long employeeId;
    private String fullName;
    private Long totalOrders;
    private Double totalSales;
    private Double avgTransactionValue;
    private Double upsellRate; // (Orders with addons / Total Orders) * 100
    private Integer totalLateMinutes;
    private Long onTimeShifts;
    private Long totalShifts;
    private Long manualDrawerOpens;
}
