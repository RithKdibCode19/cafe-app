package com.example.backend.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "Branch ID is required")
    private Long branchId;
    
    @NotNull(message = "Cashier User ID is required")
    private Long cashierUserId;
    
    private Long customerId; // Optional
    
    @NotNull(message = "Order type is required")
    private String orderType; // "DINE_IN", "TAKEAWAY", "DELIVERY"
    
    private String note;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
