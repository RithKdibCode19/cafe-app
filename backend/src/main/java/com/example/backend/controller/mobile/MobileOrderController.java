package com.example.backend.controller.mobile;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.mobile.MobileOrderDTO;
import com.example.backend.security.JwtUtils;
import com.example.backend.services.mobile.MobileOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mobile/orders")
@Tag(name = "Mobile - Orders", description = "Customer order management (auth required)")
@SecurityRequirement(name = "bearerAuth")
public class MobileOrderController {

    private final MobileOrderService mobileOrderService;
    private final JwtUtils jwtUtils;

    public MobileOrderController(MobileOrderService mobileOrderService, JwtUtils jwtUtils) {
        this.mobileOrderService = mobileOrderService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    @Operation(summary = "Place a new order", description = "Create a new TAKEAWAY or DELIVERY order")
    public ResponseEntity<?> placeOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MobileOrderDTO.PlaceOrderRequest request) {
        try {
            Long customerId = extractCustomerId(authHeader);
            MobileOrderDTO.OrderResponse response = mobileOrderService.placeOrder(customerId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get order history", description = "Returns all customer's past orders")
    public ResponseEntity<?> getOrderHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            Long customerId = extractCustomerId(authHeader);
            List<MobileOrderDTO.OrderSummary> orders = mobileOrderService.getOrderHistory(customerId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details", description = "Returns full details of a specific order (must be own order)")
    public ResponseEntity<?> getOrderDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long orderId) {
        try {
            Long customerId = extractCustomerId(authHeader);
            MobileOrderDTO.OrderResponse response = mobileOrderService.getOrderDetail(customerId, orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel a pending order", description = "Cancel an order that is still in PENDING status")
    public ResponseEntity<?> cancelOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long orderId) {
        try {
            Long customerId = extractCustomerId(authHeader);
            MobileOrderDTO.OrderResponse response = mobileOrderService.cancelOrder(customerId, orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    private Long extractCustomerId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
        Long customerId = jwtUtils.getCustomerIdFromToken(token);
        if (customerId == null) {
            throw new RuntimeException("Invalid customer token");
        }
        return customerId;
    }
}
