package com.example.backend.dto.mobile;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MobileOrderDTO {

    // ========== Request DTOs ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceOrderRequest {
        @NotNull(message = "Branch ID is required")
        private Long branchId;

        @NotNull(message = "Order type is required")
        private String orderType; // TAKEAWAY or DELIVERY

        private String note;

        @NotEmpty(message = "Order items are required")
        @Valid
        private List<OrderItemRequest> items;

        // For DELIVERY orders
        private String deliveryAddress;
        private String deliveryPhone;
        private String deliveryNote;

        // Payment
        private String paymentMethod; // QR, CASH
        private Double receivedAmount;

        // Loyalty
        private Integer pointsRedeemed;
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
        private String note;
        private Double subTotal;
        private Double discountAmount;
        private Double taxAmount;
        private Double totalAmount;
        private Double deliveryFee;
        private String deliveryAddress;
        private String deliveryPhone;
        private Integer pointsRedeemed;
        private String branchName;
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
        private String variantSize;
        private Integer qty;
        private Double unitPrice;
        private List<String> addonNames;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummary {
        private Long orderId;
        private String orderNo;
        private String orderType;
        private String status;
        private Double totalAmount;
        private String branchName;
        private Integer itemCount;
        private String createdAt;
    }

    // ========== Auth DTOs ==========

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FirebaseAuthRequest {
        private String firebaseToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhonePasswordRegisterRequest {
        private String phone;
        private String password;
        private String name;
        private String fcmToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhonePasswordLoginRequest {
        private String phone;
        private String password;
        private String fcmToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private CustomerProfile customer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerProfile {
        private Long customerId;
        private String name;
        private String phone;
        private String email;
        private Integer loyaltyPoints;
        private Double loyaltyRedeemRate;
        private String membershipLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String name;
        private String email;
        private String address;
        private String fcmToken;
    }
}
