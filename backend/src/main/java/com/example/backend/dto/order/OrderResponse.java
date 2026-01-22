package com.example.backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long orderId;
    private String orderNo;
    
    // Branch info
    private Long branchId;
    private String branchName;
    
    // Cashier info
    private Long cashierUserId;
    private String cashierName;
    
    // Customer info (optional)
    private Long customerId;
    private String customerName;
    
    private String orderType;
    private String status;
    private String note;
    
    private List<OrderItemResponse> items;
    private Double totalAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
