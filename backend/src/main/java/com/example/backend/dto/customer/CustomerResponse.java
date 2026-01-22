package com.example.backend.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    
    private Long customerId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private Integer totalOrders;
    private Double totalSpent;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
