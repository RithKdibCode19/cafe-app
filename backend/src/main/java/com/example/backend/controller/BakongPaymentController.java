package com.example.backend.controller;

import com.example.backend.dto.OrderRequestDTO;
import com.example.backend.dto.payment.*;
import com.example.backend.services.BakongPaymentService;
import com.example.backend.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/bakong")
public class BakongPaymentController {

    private final BakongPaymentService bakongPaymentService;
    private final OrderService orderService;

    public BakongPaymentController(BakongPaymentService bakongPaymentService, OrderService orderService) {
        this.bakongPaymentService = bakongPaymentService;
        this.orderService = orderService;
    }

    @PostMapping("/generate")
    public ResponseEntity<BakongKhqrResponseDTO> generateKhqr(@Valid @RequestBody OrderRequestDTO orderRequest) {
        // 1. Securely calculate total on server based on DB prices
        Double securedAmount = orderService.calculateTotal(orderRequest);

        // 2. Prepare the real Bakong request with internal config
        BakongKhqrRequestDTO bakongRequest = new BakongKhqrRequestDTO();
        bakongRequest.setAmount(securedAmount);
        bakongRequest.setBakongAccountId("vanny_meas@aclb"); // Configurable in real apps
        bakongRequest.setMerchantName("Cafe POS System");
        bakongRequest.setBillNumber("ORD-" + System.currentTimeMillis());
        bakongRequest.setCurrency("USD");

        return ResponseEntity.ok(bakongPaymentService.generateKhqr(bakongRequest));
    }

    @PostMapping("/check")
    public ResponseEntity<BakongPaymentCheckResponseDTO> checkPaymentStatus(
            @Valid @RequestBody BakongPaymentCheckRequestDTO request) {
        return ResponseEntity.ok(bakongPaymentService.checkPaymentStatus(request));
    }
}
