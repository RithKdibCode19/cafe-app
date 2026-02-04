package com.example.backend.dto.report;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffPerformanceReportDTO {
    private String startDate;
    private String endDate;
    private List<EmployeeStats> employeeStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
    }
}
