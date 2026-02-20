package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long orderId;
    private String orderNo;
    private BranchResponseDTO branch;
    private UserResponseDTO cashierUser;
    private CustomerResponseDTO customer;
    private String orderType;
    private String status;
    private String note;
    private Double subTotal;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private Integer pointsRedeemed;
    private String statusReason;
    private String approvedByName;
    private List<OrderItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
