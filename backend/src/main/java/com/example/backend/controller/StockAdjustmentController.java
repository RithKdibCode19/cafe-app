package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.StockAdjustmentRequestDTO;
import com.example.backend.dto.StockAdjustmentResponseDTO;
import com.example.backend.services.StockAdjustmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stock-adjustments")
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

    public StockAdjustmentController(StockAdjustmentService stockAdjustmentService) {
        this.stockAdjustmentService = stockAdjustmentService;
    }

    @PostMapping
    public ResponseEntity<StockAdjustmentResponseDTO> createAdjustment(
            @Valid @RequestBody StockAdjustmentRequestDTO request) {
        StockAdjustmentResponseDTO response = stockAdjustmentService.createAdjustment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StockAdjustmentResponseDTO>> getAllAdjustments() {
        return ResponseEntity.ok(stockAdjustmentService.getAllAdjustments());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<StockAdjustmentResponseDTO> approveAdjustment(
            @PathVariable Long id,
            @RequestParam String pinCode) {
        return ResponseEntity.ok(stockAdjustmentService.approveAdjustment(id, pinCode));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<StockAdjustmentResponseDTO> rejectAdjustment(@PathVariable Long id) {
        return ResponseEntity.ok(stockAdjustmentService.rejectAdjustment(id));
    }
}
