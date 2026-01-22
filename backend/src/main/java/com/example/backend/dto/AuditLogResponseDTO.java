package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long logId;
    private UserResponseDTO user;
    private String action;
    private String ipAddress;
    private LocalDateTime actionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
