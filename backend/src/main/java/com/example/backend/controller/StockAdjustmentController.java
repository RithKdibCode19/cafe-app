package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.StockAdjustmentRequestDTO;
import com.example.backend.dto.StockAdjustmentResponseDTO;
import com.example.backend.services.StockAdjustmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-adjustments")
@RequiredArgsConstructor
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

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
}
