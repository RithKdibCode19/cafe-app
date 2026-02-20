package com.example.backend.dto;

import lombok.Data;

@Data
public class QrClockInRequestDTO {
    private String qrToken;
    private Long employeeId;

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
}
