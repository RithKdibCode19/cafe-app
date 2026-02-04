package com.example.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.OrderRequestDTO;
import com.example.backend.dto.OrderResponseDTO;
import com.example.backend.services.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Alternative endpoint for creating orders (matches existing pattern)
     * POST /api/orders/add
     */
    @PostMapping("/add")
    public ResponseEntity<OrderResponseDTO> addOrder(@Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all orders
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Update entire order
     * PUT /api/orders/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.updateOrder(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status only
     * PUT /api/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long userId, // e.g. from current session
            @RequestParam(required = false) String reason) {

        Long effectiveUserId = (userId != null) ? userId : 1L; // Default to system/admin if null
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, effectiveUserId, reason));
    }

    /**
     * Soft delete order
     * DELETE /api/orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get orders by branch
     * GET /api/orders/branch/{branchId}
     */
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByBranch(@PathVariable Long branchId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByBranch(branchId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status
     * GET /api/orders/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status.toUpperCase());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by cashier
     * GET /api/orders/cashier/{cashierUserId}
     */
    @GetMapping("/cashier/{cashierUserId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCashier(@PathVariable Long cashierUserId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByCashier(cashierUserId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by date range
     * GET
     * /api/orders/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(start, end);
        return ResponseEntity.ok(orders);
    }

    /**
     * Search orders by order number
     * GET /api/orders/search?orderNo=ORD-123
     */
    @GetMapping("/search")
    public ResponseEntity<List<OrderResponseDTO>> searchOrdersByOrderNo(
            @RequestParam String orderNo) {
        List<OrderResponseDTO> orders = orderService.searchOrdersByOrderNo(orderNo);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get today's orders for a branch
     * GET /api/orders/today/{branchId}
     */
    @GetMapping("/today/{branchId}")
    public ResponseEntity<List<OrderResponseDTO>> getTodayOrdersForBranch(@PathVariable Long branchId) {
        List<OrderResponseDTO> orders = orderService.getTodayOrdersForBranch(branchId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get pending orders (orders with PENDING status)
     * GET /api/orders/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponseDTO>> getPendingOrders() {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus("PENDING");
        return ResponseEntity.ok(orders);
    }

    /**
     * Get paid orders (orders with PAID status)
     * GET /api/orders/paid
     */
    @GetMapping("/paid")
    public ResponseEntity<List<OrderResponseDTO>> getPaidOrders() {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus("PAID");
        return ResponseEntity.ok(orders);
    }

    /**
     * Get voided orders (orders with VOID status)
     * GET /api/orders/void
     */
    @GetMapping("/void")
    public ResponseEntity<List<OrderResponseDTO>> getVoidOrders() {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus("VOID");
        return ResponseEntity.ok(orders);
    }

    /**
     * Mark order as paid
     * PUT /api/orders/{id}/pay
     */
    @PutMapping("/{id}/pay")
    public ResponseEntity<OrderResponseDTO> markOrderAsPaid(@PathVariable Long id) {
        // Assume User ID 1 for now
        OrderResponseDTO response = orderService.updateOrderStatus(id, "PAID", 1L, "Payment Received");
        return ResponseEntity.ok(response);
    }

    /**
     * Void an order
     * PUT /api/orders/{id}/void
     */
    @PutMapping("/{id}/void")
    public ResponseEntity<OrderResponseDTO> voidOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        OrderResponseDTO response = orderService.updateOrderStatus(id, "VOID", 1L, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Refund an order
     * PUT /api/orders/{id}/refund
     */
    @PutMapping("/{id}/refund")
    public ResponseEntity<OrderResponseDTO> refundOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        OrderResponseDTO response = orderService.updateOrderStatus(id, "REFUND", 1L, reason);
        return ResponseEntity.ok(response);
    }
}
