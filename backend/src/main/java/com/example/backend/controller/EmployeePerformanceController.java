package com.example.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.EmployeePerformanceDTO;
import com.example.backend.services.EmployeePerformanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff-performance")
@RequiredArgsConstructor
public class EmployeePerformanceController {

    private final EmployeePerformanceService performanceService;

    @GetMapping
    public ResponseEntity<List<EmployeePerformanceDTO>> getPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        return ResponseEntity.ok(performanceService.getPerformanceReport(start, end));
    }
}
