package com.example.backend.controller.publicapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.publicapi.PublicOrderDTO;
import com.example.backend.services.publicapi.PublicOrderService;

import jakarta.validation.Valid;

/**
 * Public order endpoints (no auth required).
 * Used by QR code web menu for guest ordering.
 */
@RestController
@RequestMapping("/api/public/orders")
public class PublicOrderController {

    private final PublicOrderService publicOrderService;

    public PublicOrderController(PublicOrderService publicOrderService) {
        this.publicOrderService = publicOrderService;
    }

    /**
     * Place a guest order.
     * POST /api/public/orders
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody PublicOrderDTO.PlaceOrderRequest request) {
        try {
            PublicOrderDTO.OrderResponse response = publicOrderService.placeOrder(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get order status by order number (for polling/tracking).
     * GET /api/public/orders/{orderNo}/status
     */
    @GetMapping("/{orderNo}/status")
    public ResponseEntity<?> getOrderStatus(@PathVariable String orderNo) {
        PublicOrderDTO.OrderStatusResponse status = publicOrderService.getOrderStatus(orderNo);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    /**
     * Get full order details by order number.
     * GET /api/public/orders/{orderNo}
     */
    @GetMapping("/{orderNo}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String orderNo) {
        PublicOrderDTO.OrderResponse response = publicOrderService.getOrderByOrderNo(orderNo);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
