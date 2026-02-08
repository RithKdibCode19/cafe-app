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
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.ShiftRequestDTO;
import com.example.backend.dto.ShiftResponseDTO;
import com.example.backend.services.ShiftService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping
    public ResponseEntity<ShiftResponseDTO> createShift(@Valid @RequestBody ShiftRequestDTO request) {
        ShiftResponseDTO response = shiftService.createShift(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShiftResponseDTO>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShiftResponseDTO>> getShiftsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(shiftService.getShiftsByEmployee(employeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> updateShift(@PathVariable Long id,
            @Valid @RequestBody ShiftRequestDTO request) {
        return ResponseEntity.ok(shiftService.updateShift(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    public ResponseEntity<ShiftResponseDTO> getCurrentShift(@org.springframework.web.bind.annotation.RequestParam Long userId) {
        ShiftResponseDTO shift = shiftService.getCurrentShift(userId);
        return ResponseEntity.ok(shift); 
    }

    @GetMapping("/summary")
    public ResponseEntity<com.example.backend.dto.report.ShiftSummaryDTO> getShiftSummary(
            @org.springframework.web.bind.annotation.RequestParam Long userId) {
        return ResponseEntity.ok(shiftService.getShiftSummary(userId));
    }

    @PostMapping("/open")
    public ResponseEntity<ShiftResponseDTO> openShift(
            @org.springframework.web.bind.annotation.RequestParam Long userId,
            @org.springframework.web.bind.annotation.RequestParam Double startingCash) {
        return ResponseEntity.ok(shiftService.openShift(userId, startingCash));
    }

    @PostMapping("/close")
    public ResponseEntity<ShiftResponseDTO> closeShift(
            @org.springframework.web.bind.annotation.RequestParam Long userId,
            @org.springframework.web.bind.annotation.RequestParam Double endingCash) {
        return ResponseEntity.ok(shiftService.closeShift(userId, endingCash));
    }
}
