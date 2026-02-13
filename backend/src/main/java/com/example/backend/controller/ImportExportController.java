package com.example.backend.controller;

import com.example.backend.dto.IngredientResponseDTO;
import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.dto.OrderResponseDTO;
import com.example.backend.services.ExportService;
import com.example.backend.services.ImportService;
import com.example.backend.services.IngredientService;
import com.example.backend.services.MenuItemService;
import com.example.backend.services.OrderService;
import com.example.backend.services.ReportService;
import com.example.backend.dto.report.StockMovementReportDTO;
import com.example.backend.dto.report.StaffPerformanceReportDTO;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/import-export")
public class ImportExportController {

    private final ExportService exportService;
    private final ImportService importService;
    private final MenuItemService menuItemService;
    private final OrderService orderService;
    private final IngredientService ingredientService;
    private final ReportService reportService;

    public ImportExportController(ExportService exportService,
                                  ImportService importService,
                                  MenuItemService menuItemService,
                                  OrderService orderService,
                                  IngredientService ingredientService,
                                  ReportService reportService) {
        this.exportService = exportService;
        this.importService = importService;
        this.menuItemService = menuItemService;
        this.orderService = orderService;
        this.ingredientService = ingredientService;
        this.reportService = reportService;
    }

    @GetMapping("/export/menu-items/excel")
    public ResponseEntity<byte[]> exportMenuItemsExcel() throws IOException {
        List<MenuItemResponseDTO> items = menuItemService.getAllMenuItems(null);
        byte[] content = exportService.exportMenuItemsToExcel(items);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=menu_items.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @GetMapping("/export/menu-items/csv")
    public ResponseEntity<String> exportMenuItemsCsv() throws IOException {
        List<MenuItemResponseDTO> items = menuItemService.getAllMenuItems(null);
        String content = exportService.exportMenuItemsToCsv(items);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=menu_items.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping("/export/ingredients/excel")
    public ResponseEntity<byte[]> exportIngredientsExcel() throws IOException {
        List<IngredientResponseDTO> items = ingredientService.getAllIngredients();
        byte[] content = exportService.exportIngredientsToExcel(items);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingredients.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @GetMapping("/export/ingredients/csv")
    public ResponseEntity<String> exportIngredientsCsv() throws IOException {
        List<IngredientResponseDTO> items = ingredientService.getAllIngredients();
        String content = exportService.exportIngredientsToCsv(items);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingredients.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping("/export/sales")
    public ResponseEntity<byte[]> exportSalesExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) throws IOException {
        
        List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(start, end);
        byte[] content = exportService.exportSalesToExcel(orders);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @GetMapping("/export/inventory")
    public ResponseEntity<byte[]> exportInventoryAudit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long branchId) throws IOException {
        StockMovementReportDTO report = reportService.getStockMovementReport(start, end, branchId, null);
        byte[] content = exportService.exportStockMovementToExcel(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory_audit.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @GetMapping("/export/staff")
    public ResponseEntity<byte[]> exportStaffPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) throws IOException {
        StaffPerformanceReportDTO report = reportService.getStaffPerformanceReport(start, end);
        byte[] content = exportService.exportStaffPerformanceToExcel(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff_productivity.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @PostMapping("/import/menu-items")
    public ResponseEntity<String> importMenuItems(@RequestParam("file") MultipartFile file) {
        try {
            importService.importMenuItems(file);
            return ResponseEntity.ok("Import successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
        }
    }

    @PostMapping("/import/stock")
    public ResponseEntity<String> importStock(@RequestParam("branchId") Long branchId,
                                               @RequestParam("file") MultipartFile file) {
        try {
            importService.importIngredientStock(branchId, file);
            return ResponseEntity.ok("Stock update successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Stock update failed: " + e.getMessage());
        }
    }
}
