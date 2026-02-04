package com.example.backend.controller;

import com.example.backend.dto.payment.*;
import com.example.backend.services.BakongPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/bakong")
@RequiredArgsConstructor
public class BakongPaymentController {

    private final BakongPaymentService bakongPaymentService;

    @PostMapping("/generate")
    public ResponseEntity<BakongKhqrResponseDTO> generateKhqr(@RequestBody BakongKhqrRequestDTO request) {
        return ResponseEntity.ok(bakongPaymentService.generateKhqr(request));
    }

    @PostMapping("/check")
    public ResponseEntity<BakongPaymentCheckResponseDTO> checkPaymentStatus(
            @RequestBody BakongPaymentCheckRequestDTO request) {
        return ResponseEntity.ok(bakongPaymentService.checkPaymentStatus(request));
    }
}
