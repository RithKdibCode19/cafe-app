package com.example.backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotBlank(message = "Order number is required")
    private String orderNo;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotNull(message = "Cashier user ID is required")
    private Long cashierUserId;

    private Long customerId;

    @NotBlank(message = "Order type is required")
    private String orderType;

    @NotBlank(message = "Status is required")
    private String status;

    private String note;

    @NotEmpty(message = "Order items are required")
    private List<OrderItemRequestDTO> items;
}
