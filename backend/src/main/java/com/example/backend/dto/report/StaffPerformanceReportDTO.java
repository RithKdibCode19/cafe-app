package com.example.backend.dto.report;

import java.util.List;

public class StaffPerformanceReportDTO {
    private String startDate;
    private String endDate;
    private List<EmployeeStats> employeeStats;

    public StaffPerformanceReportDTO() {}

    public StaffPerformanceReportDTO(String startDate, String endDate, List<EmployeeStats> employeeStats) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.employeeStats = employeeStats;
    }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public List<EmployeeStats> getEmployeeStats() { return employeeStats; }
    public void setEmployeeStats(List<EmployeeStats> employeeStats) { this.employeeStats = employeeStats; }

    public static class EmployeeStats {
        private Long employeeId;
        private String fullName;
        private String position;
        private Integer totalOrdersHandled;
        private Double totalSalesGenerated;
        private Double averageOrderValue;
        private Long totalMinutesWorked;
        private Long lateOccurrences;
        private Double punctualityRate; // Percentage of non-late clock-ins

        public EmployeeStats() {}

        public EmployeeStats(Long employeeId, String fullName, String position, Integer totalOrdersHandled, Double totalSalesGenerated, Double averageOrderValue, Long totalMinutesWorked, Long lateOccurrences, Double punctualityRate) {
            this.employeeId = employeeId;
            this.fullName = fullName;
            this.position = position;
            this.totalOrdersHandled = totalOrdersHandled;
            this.totalSalesGenerated = totalSalesGenerated;
            this.averageOrderValue = averageOrderValue;
            this.totalMinutesWorked = totalMinutesWorked;
            this.lateOccurrences = lateOccurrences;
            this.punctualityRate = punctualityRate;
        }

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public Integer getTotalOrdersHandled() { return totalOrdersHandled; }
        public void setTotalOrdersHandled(Integer totalOrdersHandled) { this.totalOrdersHandled = totalOrdersHandled; }
        public Double getTotalSalesGenerated() { return totalSalesGenerated; }
        public void setTotalSalesGenerated(Double totalSalesGenerated) { this.totalSalesGenerated = totalSalesGenerated; }
        public Double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(Double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
        public Long getTotalMinutesWorked() { return totalMinutesWorked; }
        public void setTotalMinutesWorked(Long totalMinutesWorked) { this.totalMinutesWorked = totalMinutesWorked; }
        public Long getLateOccurrences() { return lateOccurrences; }
        public void setLateOccurrences(Long lateOccurrences) { this.lateOccurrences = lateOccurrences; }
        public Double getPunctualityRate() { return punctualityRate; }
        public void setPunctualityRate(Double punctualityRate) { this.punctualityRate = punctualityRate; }
    }
}
