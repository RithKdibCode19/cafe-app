package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.common.ApiResponse;
import com.example.backend.dto.report.ReceiptDTO;
import com.example.backend.services.ReceiptService;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<ReceiptDTO>> getReceiptByOrderId(@PathVariable Long orderId) {
        try {
            ReceiptDTO receipt = receiptService.generateReceipt(orderId);
            return ResponseEntity.ok(ApiResponse.success(receipt, "Receipt generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/by-order-no")
    public ResponseEntity<ApiResponse<ReceiptDTO>> getReceiptByOrderNo(@RequestParam String orderNo) {
        try {
            ReceiptDTO receipt = receiptService.generateReceiptByOrderNo(orderNo);
            return ResponseEntity.ok(ApiResponse.success(receipt, "Receipt generated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
