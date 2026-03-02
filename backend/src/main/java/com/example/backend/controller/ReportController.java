package com.example.backend.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.cache.annotation.CacheEvict;

import com.example.backend.dto.common.ApiResponse;
import com.example.backend.dto.report.DashboardStatsDTO;
import com.example.backend.dto.report.SalesReportDTO;
import com.example.backend.dto.report.StockTransferResponseDTO;
import com.example.backend.services.ReportService;
import com.example.backend.seeder.DataSeeder;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final DataSeeder dataSeeder;

    public ReportController(ReportService reportService, DataSeeder dataSeeder) {
        this.reportService = reportService;
        this.dataSeeder = dataSeeder;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = reportService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard stats retrieved successfully"));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<SalesReportDTO>> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 30 days if not specified
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }

        SalesReportDTO report = reportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Sales report generated successfully"));
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<com.example.backend.dto.report.InventoryReportDTO>> getInventoryReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long branchId) {

        if (endDate == null)
            endDate = LocalDate.now();
        if (startDate == null)
            startDate = endDate.minusDays(30);

        com.example.backend.dto.report.InventoryReportDTO report = reportService.getInventoryReport(startDate, endDate, branchId);
        return ResponseEntity.ok(ApiResponse.success(report, "Inventory report generated successfully"));
    }

    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<com.example.backend.dto.report.StaffPerformanceReportDTO>> getStaffPerformanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (endDate == null)
            endDate = LocalDate.now();
        if (startDate == null)
            startDate = endDate.minusDays(30);

        com.example.backend.dto.report.StaffPerformanceReportDTO report = reportService
                .getStaffPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Staff performance report generated successfully"));
    }

    @GetMapping("/shift/{employeeId}")
    public ResponseEntity<ApiResponse<com.example.backend.dto.report.ShiftSummaryDTO>> getShiftSummary(
            @PathVariable Long employeeId) {
        com.example.backend.dto.report.ShiftSummaryDTO summary = reportService.getCurrentShiftSummary(employeeId);
        return ResponseEntity.ok(ApiResponse.success(summary, "Current shift summary retrieved"));
    }

    @GetMapping("/stock-movement")
    public ResponseEntity<ApiResponse<com.example.backend.dto.report.StockMovementReportDTO>> getStockMovementReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long ingredientId) {

        if (endDate == null)
            endDate = LocalDate.now();
        if (startDate == null)
            startDate = endDate.minusDays(30);

        com.example.backend.dto.report.StockMovementReportDTO report = reportService.getStockMovementReport(startDate,
                endDate, branchId, ingredientId);
        return ResponseEntity.ok(ApiResponse.success(report, "Stock movement report generated successfully"));
    }

    @GetMapping("/transfers")
    public ResponseEntity<ApiResponse<List<StockTransferResponseDTO>>> getStockTransfers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long branchId) {

        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusDays(30);

        List<StockTransferResponseDTO> transfers = reportService.getStockTransfers(startDate, endDate, branchId);
        return ResponseEntity.ok(ApiResponse.success(transfers, "Stock transfer history retrieved successfully"));
    }

    @PostMapping("/seed-inventory")
    @CacheEvict(value = "menu-items", allEntries = true)
    public ResponseEntity<ApiResponse<String>> seedInventoryData() {
        try {
            dataSeeder.seedRealisticInventoryData();
            return ResponseEntity.ok(ApiResponse.success("Success", "Realistic inventory data seeded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to seed inventory data: " + e.getMessage()));
        }
    }
}
