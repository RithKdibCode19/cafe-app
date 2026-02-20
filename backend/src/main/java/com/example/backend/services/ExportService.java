package com.example.backend.services;

import com.example.backend.dto.IngredientResponseDTO;
import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.dto.OrderResponseDTO;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportMenuItemsToExcel(List<MenuItemResponseDTO> items) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Menu Items");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Category", "Base Price", "Available", "Low Stock"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Data
            int rowIdx = 1;
            for (MenuItemResponseDTO item : items) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getMenuItemId());
                row.createCell(1).setCellValue(item.getName());
                row.createCell(2).setCellValue(item.getCategory() != null ? item.getCategory().getName() : "");
                row.createCell(3).setCellValue(item.getBasePrice());
                row.createCell(4).setCellValue(item.getIsAvailable() != null && item.getIsAvailable() ? "Yes" : "No");
                row.createCell(5).setCellValue(item.getLowStock() != null && item.getLowStock() ? "Yes" : "No");
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public String exportMenuItemsToCsv(List<MenuItemResponseDTO> items) throws IOException {
        try (StringWriter sw = new StringWriter(); CSVWriter csvWriter = new CSVWriter(sw)) {
            String[] header = {"ID", "Name", "Category", "Base Price", "Available", "Low Stock"};
            csvWriter.writeNext(header);

            for (MenuItemResponseDTO item : items) {
                String[] data = {
                        String.valueOf(item.getMenuItemId()),
                        item.getName(),
                        item.getCategory() != null ? item.getCategory().getName() : "",
                        String.valueOf(item.getBasePrice()),
                        item.getIsAvailable() != null && item.getIsAvailable() ? "TRUE" : "FALSE",
                        item.getLowStock() != null && item.getLowStock() ? "TRUE" : "FALSE"
                };
                csvWriter.writeNext(data);
            }
            return sw.toString();
        }
    }

    public byte[] exportSalesToExcel(List<OrderResponseDTO> orders) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sales Report");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Order No", "Date", "Branch", "Cashier", "Customer", "Subtotal", "Discount", "Tax", "Total", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (OrderResponseDTO order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getOrderNo());
                row.createCell(1).setCellValue(order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FORMATTER) : "");
                row.createCell(2).setCellValue(order.getBranch() != null ? order.getBranch().getName() : "");
                
                String cashierName = "";
                if (order.getCashierUser() != null && order.getCashierUser().getEmployee() != null) {
                    cashierName = order.getCashierUser().getEmployee().getFullName();
                }
                row.createCell(3).setCellValue(cashierName);
                
                row.createCell(4).setCellValue(order.getCustomer() != null ? order.getCustomer().getFullName() : "Walk-in");
                row.createCell(5).setCellValue(order.getSubTotal() != null ? order.getSubTotal() : 0.0);
                row.createCell(6).setCellValue(order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0);
                row.createCell(7).setCellValue(order.getTaxAmount() != null ? order.getTaxAmount() : 0.0);
                row.createCell(8).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
                row.createCell(9).setCellValue(order.getStatus());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportIngredientsToExcel(List<IngredientResponseDTO> ingredients) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventory Portfolio");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "SKU", "Name", "Unit", "Reorder Level", "Current Stock", "Cost Per Unit"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (IngredientResponseDTO ing : ingredients) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(ing.getIngredientId());
                row.createCell(1).setCellValue(ing.getSku() != null ? ing.getSku() : "");
                row.createCell(2).setCellValue(ing.getName());
                row.createCell(3).setCellValue(ing.getUnit());
                row.createCell(4).setCellValue(ing.getReorderLevel() != null ? ing.getReorderLevel() : 0.0);
                row.createCell(5).setCellValue(ing.getCurrentStock() != null ? ing.getCurrentStock() : 0.0);
                row.createCell(6).setCellValue(ing.getCostPerUnit() != null ? ing.getCostPerUnit() : 0.0);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public String exportIngredientsToCsv(List<IngredientResponseDTO> ingredients) throws IOException {
        try (StringWriter sw = new StringWriter(); CSVWriter csvWriter = new CSVWriter(sw)) {
            String[] header = {"ID", "SKU", "Name", "Unit", "Reorder Level", "Current Stock", "Cost Per Unit"};
            csvWriter.writeNext(header);

            for (IngredientResponseDTO ing : ingredients) {
                String[] data = {
                        String.valueOf(ing.getIngredientId()),
                        ing.getSku() != null ? ing.getSku() : "",
                        ing.getName(),
                        ing.getUnit(),
                        String.valueOf(ing.getReorderLevel() != null ? ing.getReorderLevel() : 0.0),
                        String.valueOf(ing.getCurrentStock() != null ? ing.getCurrentStock() : 0.0),
                        String.valueOf(ing.getCostPerUnit() != null ? ing.getCostPerUnit() : 0.0)
                };
                csvWriter.writeNext(data);
            }
            return sw.toString();
        }
    }

    public byte[] exportStockMovementToExcel(com.example.backend.dto.report.StockMovementReportDTO report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Stock Movement Audit");

            // Info Rows
            Row dateRow = sheet.createRow(0);
            dateRow.createCell(0).setCellValue("Period:");
            dateRow.createCell(1).setCellValue(report.getStartDate() + " to " + report.getEndDate());

            // Header
            Row headerRow = sheet.createRow(2);
            String[] columns = {"Ingredient", "SKU", "Opening", "Received (+)", "Sold (-)", "Adjusted (+/-)", "Closing", "Wastage %", "Unit"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Data
            int rowIdx = 3;
            for (com.example.backend.dto.report.StockMovementReportDTO.StockMovementItem item : report.getMovements()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getName());
                row.createCell(1).setCellValue(item.getSku());
                row.createCell(2).setCellValue(item.getOpeningStock());
                row.createCell(3).setCellValue(item.getReceived());
                row.createCell(4).setCellValue(item.getSold());
                row.createCell(5).setCellValue(item.getAdjusted());
                row.createCell(6).setCellValue(item.getClosingStock());
                row.createCell(7).setCellValue(item.getWastagePercentage());
                row.createCell(8).setCellValue(item.getUnit());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportStaffPerformanceToExcel(com.example.backend.dto.report.StaffPerformanceReportDTO report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Staff Productivity");

            // Info Rows
            Row dateRow = sheet.createRow(0);
            dateRow.createCell(0).setCellValue("Period:");
            dateRow.createCell(1).setCellValue(report.getStartDate() + " to " + report.getEndDate());

            // Header
            Row headerRow = sheet.createRow(2);
            String[] columns = {"Employee Name", "Position", "Total Orders", "Total Sales", "Avg Order Value", "Total Hours", "Late Occurrences", "Punctuality Rate %"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Data
            int rowIdx = 3;
            for (com.example.backend.dto.report.StaffPerformanceReportDTO.EmployeeStats stat : report.getEmployeeStats()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(stat.getFullName());
                row.createCell(1).setCellValue(stat.getPosition());
                row.createCell(2).setCellValue(stat.getTotalOrdersHandled());
                row.createCell(3).setCellValue(stat.getTotalSalesGenerated());
                row.createCell(4).setCellValue(stat.getAverageOrderValue());
                row.createCell(5).setCellValue(stat.getTotalMinutesWorked() / 60.0);
                row.createCell(6).setCellValue(stat.getLateOccurrences());
                row.createCell(7).setCellValue(stat.getPunctualityRate());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
