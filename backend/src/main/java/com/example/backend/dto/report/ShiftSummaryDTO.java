package com.example.backend.dto.report;

import java.util.List;
import java.util.Map;

public class ShiftSummaryDTO {
    private Long employeeId;
    private String employeeName;
    private String checkInTime;
    private String checkOutTime;
    private String status;
    private Double totalSales;
    private java.util.Map<String, Double> salesByMethod;
    private Integer totalOrders;
    private Double expectedCash;
    private Double actualCash;
    private Double discrepancy;
    private Double startingCash;

    public ShiftSummaryDTO() {}

    public ShiftSummaryDTO(Long employeeId, String employeeName, String checkInTime, String checkOutTime, String status, Double totalSales, java.util.Map<String, Double> salesByMethod, Integer totalOrders, Double expectedCash, Double actualCash, Double discrepancy, Double startingCash) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
        this.totalSales = totalSales;
        this.salesByMethod = salesByMethod;
        this.totalOrders = totalOrders;
        this.expectedCash = expectedCash;
        this.actualCash = actualCash;
        this.discrepancy = discrepancy;
        this.startingCash = startingCash;
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }
    public java.util.Map<String, Double> getSalesByMethod() { return salesByMethod; }
    public void setSalesByMethod(java.util.Map<String, Double> salesByMethod) { this.salesByMethod = salesByMethod; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public Double getExpectedCash() { return expectedCash; }
    public void setExpectedCash(Double expectedCash) { this.expectedCash = expectedCash; }
    public Double getActualCash() { return actualCash; }
    public void setActualCash(Double actualCash) { this.actualCash = actualCash; }
    public Double getDiscrepancy() { return discrepancy; }
    public void setDiscrepancy(Double discrepancy) { this.discrepancy = discrepancy; }
    public Double getStartingCash() { return startingCash; }
    public void setStartingCash(Double startingCash) { this.startingCash = startingCash; }
}
