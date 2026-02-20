package com.example.backend.dto.publicapi;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PublicOrderDTO {

    // ========== Request DTOs ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceOrderRequest {
        @NotNull(message = "Branch code is required")
        private String branchCode;

        private String tableNo;

        @NotNull(message = "Order type is required")
        private String orderType; // DINE_IN or TAKEAWAY

        private String note;

        private String customerName; // Optional for guest
        private String customerPhone; // Optional for guest

        @NotEmpty(message = "Order items are required")
        @Valid
        private List<OrderItemRequest> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Menu item ID is required")
        private Long menuItemId;

        private Long variantId;

        @NotNull(message = "Quantity is required")
        private Integer qty;

        private List<Long> addonIds;

        private String note;
    }

    // ========== Response DTOs ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long orderId;
        private String orderNo;
        private String orderType;
        private String status;
        private String tableNo;
        private String note;
        private Double subTotal;
        private Double taxAmount;
        private Double totalAmount;
        private String branchName;
        private String customerName;
        private List<OrderItemResponse> items;
        private String createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long orderItemId;
        private String menuItemName;
        private String menuItemImage;
        private String variantSize;
        private Integer qty;
        private Double unitPrice;
        private List<String> addonNames;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusResponse {
        private String orderNo;
        private String status;
        private String tableNo;
        private String branchName;
        private Double totalAmount;
        private Integer itemCount;
        private String createdAt;
        private String updatedAt;
    }
}
