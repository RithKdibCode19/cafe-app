package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.BranchPerformanceDTO;
import com.example.backend.services.BranchInsightService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/branches/insights")
@RequiredArgsConstructor
public class BranchInsightController {

    private final BranchInsightService branchInsightService;

    @GetMapping("/comparison")
    public ResponseEntity<List<BranchPerformanceDTO>> getBranchComparison(
            @RequestParam(defaultValue = "TODAY") String range) {
        return ResponseEntity.ok(branchInsightService.getBranchComparison(range));
    }
}
