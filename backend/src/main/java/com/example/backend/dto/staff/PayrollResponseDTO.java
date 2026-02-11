package com.example.backend.dto.staff;

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

    public PayrollResponseDTO() {}

    public PayrollResponseDTO(Long employeeId, String fullName, String position, Double baseSalary, Double hourlyRate, Double totalHoursWorked, Double hourlyEarnings, Double totalEarnings, Integer daysWorked, Integer lateOccurrences, Integer totalLateMinutes) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.position = position;
        this.baseSalary = baseSalary;
        this.hourlyRate = hourlyRate;
        this.totalHoursWorked = totalHoursWorked;
        this.hourlyEarnings = hourlyEarnings;
        this.totalEarnings = totalEarnings;
        this.daysWorked = daysWorked;
        this.lateOccurrences = lateOccurrences;
        this.totalLateMinutes = totalLateMinutes;
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(Double baseSalary) { this.baseSalary = baseSalary; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public Double getTotalHoursWorked() { return totalHoursWorked; }
    public void setTotalHoursWorked(Double totalHoursWorked) { this.totalHoursWorked = totalHoursWorked; }

    public Double getHourlyEarnings() { return hourlyEarnings; }
    public void setHourlyEarnings(Double hourlyEarnings) { this.hourlyEarnings = hourlyEarnings; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }

    public Integer getDaysWorked() { return daysWorked; }
    public void setDaysWorked(Integer daysWorked) { this.daysWorked = daysWorked; }

    public Integer getLateOccurrences() { return lateOccurrences; }
    public void setLateOccurrences(Integer lateOccurrences) { this.lateOccurrences = lateOccurrences; }

    public Integer getTotalLateMinutes() { return totalLateMinutes; }
    public void setTotalLateMinutes(Integer totalLateMinutes) { this.totalLateMinutes = totalLateMinutes; }
}
