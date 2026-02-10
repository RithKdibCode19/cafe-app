package com.example.backend.dto;

import lombok.Data;

@Data
public class QrClockInRequestDTO {
    private String qrToken;
    private Long employeeId;
}
