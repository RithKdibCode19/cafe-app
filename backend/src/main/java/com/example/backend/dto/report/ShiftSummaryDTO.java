package com.example.backend.dto.report;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ShiftSummaryDTO {
    private Long employeeId;
    private String employeeName;
    private String checkInTime;
    private String checkOutTime;
    private Double startingCash;

    // Financials
    private Double totalSales;
    private Map<String, Double> salesByMethod;
    private Integer totalOrders;

    // Drawer Status
    private Double expectedCash;
    private Double actualCash; // Input by user
    private Double discrepancy;

    private String status; // OPEN or CLOSED
}
