package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    
    private Long customerId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String gender;
    private java.time.LocalDate dob;
    private Integer loyaltyPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
