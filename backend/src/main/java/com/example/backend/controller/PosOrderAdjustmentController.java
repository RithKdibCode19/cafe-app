package com.example.backend.controller;

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

import com.example.backend.dto.PosOrderAdjustmentRequestDTO;
import com.example.backend.dto.PosOrderAdjustmentResponseDTO;
import com.example.backend.services.PosOrderAdjustmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pos-order-adjustments")
@RequiredArgsConstructor
public class PosOrderAdjustmentController {

    private final PosOrderAdjustmentService posOrderAdjustmentService;

    @PostMapping
    public ResponseEntity<PosOrderAdjustmentResponseDTO> createAdjustment(
            @Valid @RequestBody PosOrderAdjustmentRequestDTO request) {
        PosOrderAdjustmentResponseDTO response = posOrderAdjustmentService.createAdjustment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PosOrderAdjustmentResponseDTO>> getAllAdjustments() {
        List<PosOrderAdjustmentResponseDTO> adjustments = posOrderAdjustmentService.getAllAdjustments();
        return ResponseEntity.ok(adjustments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PosOrderAdjustmentResponseDTO> getAdjustmentById(@PathVariable Long id) {
        PosOrderAdjustmentResponseDTO adjustment = posOrderAdjustmentService.getAdjustmentById(id);
        return ResponseEntity.ok(adjustment);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PosOrderAdjustmentResponseDTO> approveAdjustment(@PathVariable Long id,
            @RequestParam Long approvedBy) {
        PosOrderAdjustmentResponseDTO approvedAdjustment = posOrderAdjustmentService.approveAdjustment(id, approvedBy);
        return ResponseEntity.ok(approvedAdjustment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdjustment(@PathVariable Long id) {
        posOrderAdjustmentService.deleteAdjustment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PosOrderAdjustmentResponseDTO>> getAdjustmentsByOrderId(@PathVariable Long orderId) {
        List<PosOrderAdjustmentResponseDTO> adjustments = posOrderAdjustmentService.getAdjustmentsByOrderId(orderId);
        return ResponseEntity.ok(adjustments);
    }

    @GetMapping("/type/{adjustmentType}")
    public ResponseEntity<List<PosOrderAdjustmentResponseDTO>> getAdjustmentsByType(
            @PathVariable String adjustmentType) {
        List<PosOrderAdjustmentResponseDTO> adjustments = posOrderAdjustmentService
                .getAdjustmentsByType(adjustmentType);
        return ResponseEntity.ok(adjustments);
    }
}