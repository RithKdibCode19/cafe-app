package com.example.backend.controller.mobile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.payment.BakongKhqrRequestDTO;
import com.example.backend.dto.payment.BakongKhqrResponseDTO;
import com.example.backend.dto.payment.BakongPaymentCheckRequestDTO;
import com.example.backend.dto.payment.BakongPaymentCheckResponseDTO;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.PaymentEntity;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.security.JwtUtils;
import com.example.backend.services.BakongPaymentService;
import com.example.backend.services.SystemSettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/mobile/payments")
@Tag(name = "Mobile - Payments", description = "Customer payment endpoints (Bakong KHQR)")
@SecurityRequirement(name = "bearerAuth")
public class MobilePaymentController {

    private final BakongPaymentService bakongPaymentService;
    private final OrderRepository orderRepository;
    private final JwtUtils jwtUtils;
    private final SystemSettingService systemSettingService;
    private final PaymentRepository paymentRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MobilePaymentController.class);

    public MobilePaymentController(BakongPaymentService bakongPaymentService,
            OrderRepository orderRepository, JwtUtils jwtUtils, 
            SystemSettingService systemSettingService, PaymentRepository paymentRepository) {
        this.bakongPaymentService = bakongPaymentService;
        this.orderRepository = orderRepository;
        this.jwtUtils = jwtUtils;
        this.systemSettingService = systemSettingService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/khqr/{orderId}")
    @Operation(summary = "Generate Bakong KHQR", description = "Generate a Bakong KHQR QR code for payment of the specified order")
    public ResponseEntity<?> generateKhqr(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long orderId) {
        try {
            Long customerId = extractCustomerId(authHeader);

            // Find order and verify ownership
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (order.getCustomer() == null || !order.getCustomer().getCustomerId().equals(customerId)) {
                throw new RuntimeException("Order not found");
            }

            if (order.getStatus() != OrderEntity.OrderStatus.PENDING) {
                throw new RuntimeException("Order is not in PENDING status");
            }

            // Build KHQR request
            BakongKhqrRequestDTO khqrRequest = new BakongKhqrRequestDTO();
            khqrRequest.setAmount(order.getTotalAmount());
            khqrRequest.setCurrency("USD");
            khqrRequest.setBillNumber(order.getOrderNo());
            
            String merchantName = systemSettingService.getValue("BAKONG_MERCHANT_NAME");
            if (merchantName == null) {
                merchantName = order.getBranch() != null ? order.getBranch().getName() : "Cafe App";
            }
            khqrRequest.setMerchantName(merchantName);
            
            String bakongId = systemSettingService.getValue("BAKONG_ACCOUNT_ID");
            khqrRequest.setBakongAccountId(bakongId != null ? bakongId : "vanny_meas@aclb");

            BakongKhqrResponseDTO response = bakongPaymentService.generateKhqr(khqrRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{orderId}")
    @Operation(summary = "Check payment status", description = "Check the payment status of an order via Bakong")
    public ResponseEntity<?> checkPaymentStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long orderId,
            @RequestParam(required = false) String md5) {
        try {
            Long customerId = extractCustomerId(authHeader);

            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (order.getCustomer() == null || !order.getCustomer().getCustomerId().equals(customerId)) {
                throw new RuntimeException("Order not found");
            }

            // If order is already PAID/COMPLETED, just return current status
            if (order.getStatus() != OrderEntity.OrderStatus.PENDING) {
                return ResponseEntity.ok(java.util.Map.of(
                        "orderId", order.getOrderId(),
                        "status", order.getStatus().name(),
                        "isPaid", true));
            }

            // If MD5 provided, check with Bakong
            if (md5 != null && !md5.isBlank()) {
                log.info("Checking Bakong payment status for Order ID: {}, MD5: {}", orderId, md5);
                BakongPaymentCheckRequestDTO checkRequest = new BakongPaymentCheckRequestDTO();
                checkRequest.setMd5(md5);
                
                BakongPaymentCheckResponseDTO bakongResponse = bakongPaymentService.checkPaymentStatus(checkRequest);
                
                if (bakongResponse != null) {
                    log.info("Bakong Response for Order {}: success={}, status={}", 
                        orderId, bakongResponse.isSuccess(), 
                        (bakongResponse.getData() != null ? bakongResponse.getData().getStatus() : "N/A"));
                    
                    String bakongStatus = (bakongResponse.getData() != null && bakongResponse.getData().getStatus() != null) 
                        ? bakongResponse.getData().getStatus().toUpperCase() 
                        : "";

                    if (bakongResponse.isSuccess() && ("SUCCESS".equals(bakongStatus) || "COMPLETED".equals(bakongStatus))) {
                        log.info("Payment confirmed via Bakong for Order ID: {}", orderId);
                        
                        // Update order status to PAID
                        order.setStatus(OrderEntity.OrderStatus.PAID);
                        orderRepository.save(order);

                        // Create Payment record for consistency with POS
                        PaymentEntity payment = new PaymentEntity();
                        payment.setOrder(order);
                        payment.setPaidAmount(order.getTotalAmount());
                        payment.setChangeAmount(0.0);
                        payment.setPaymentStatus(PaymentEntity.PaymentStatus.PAID);
                        payment.setMethod(PaymentEntity.PaymentMethod.QR);
                        payment.setTransactionId(md5); // Storing MD5 as reference
                        payment.setPaidAt(java.time.LocalDateTime.now());
                        paymentRepository.save(payment);
                        
                        return ResponseEntity.ok(java.util.Map.of(
                                "orderId", order.getOrderId(),
                                "status", "PAID",
                                "isPaid", true));
                    }
                } else {
                    log.warn("Bakong Response was null for Order ID: {}", orderId);
                }
            }

            return ResponseEntity.ok(java.util.Map.of(
                    "orderId", order.getOrderId(),
                    "status", order.getStatus().name(),
                    "isPaid", false));
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
