package com.example.backend.dto.report;

import java.util.List;

public class InventoryReportDTO {
    private String startDate;
    private String endDate;
    private Double totalInventoryValue;
    private Integer lowStockItemsCount;
    private Double totalWasteCost;
    private List<StockMovement> movements;
    private List<WastageSummary> wastageSummary;

    public InventoryReportDTO() {}

    public InventoryReportDTO(String startDate, String endDate, Double totalInventoryValue, Integer lowStockItemsCount, Double totalWasteCost, List<StockMovement> movements, List<WastageSummary> wastageSummary) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalInventoryValue = totalInventoryValue;
        this.lowStockItemsCount = lowStockItemsCount;
        this.totalWasteCost = totalWasteCost;
        this.movements = movements;
        this.wastageSummary = wastageSummary;
    }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Double getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(Double totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }

    public Integer getLowStockItemsCount() { return lowStockItemsCount; }
    public void setLowStockItemsCount(Integer lowStockItemsCount) { this.lowStockItemsCount = lowStockItemsCount; }

    public Double getTotalWasteCost() { return totalWasteCost; }
    public void setTotalWasteCost(Double totalWasteCost) { this.totalWasteCost = totalWasteCost; }

    public List<StockMovement> getMovements() { return movements; }
    public void setMovements(List<StockMovement> movements) { this.movements = movements; }

    public List<WastageSummary> getWastageSummary() { return wastageSummary; }
    public void setWastageSummary(List<WastageSummary> wastageSummary) { this.wastageSummary = wastageSummary; }

    public static class StockMovement {
        private Long ingredientId;
        private String name;
        private Double openingStock;
        private Double stockIn;
        private Double consumed;
        private Double adjustments;
        private Double closingStock;
        private String unit;

        public StockMovement() {}

        public StockMovement(Long ingredientId, String name, Double openingStock, Double stockIn, Double consumed, Double adjustments, Double closingStock, String unit) {
            this.ingredientId = ingredientId;
            this.name = name;
            this.openingStock = openingStock;
            this.stockIn = stockIn;
            this.consumed = consumed;
            this.adjustments = adjustments;
            this.closingStock = closingStock;
            this.unit = unit;
        }

        // Getters and Setters
        public Long getIngredientId() { return ingredientId; }
        public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getOpeningStock() { return openingStock; }
        public void setOpeningStock(Double openingStock) { this.openingStock = openingStock; }
        public Double getStockIn() { return stockIn; }
        public void setStockIn(Double stockIn) { this.stockIn = stockIn; }
        public Double getConsumed() { return consumed; }
        public void setConsumed(Double consumed) { this.consumed = consumed; }
        public Double getAdjustments() { return adjustments; }
        public void setAdjustments(Double adjustments) { this.adjustments = adjustments; }
        public Double getClosingStock() { return closingStock; }
        public void setClosingStock(Double closingStock) { this.closingStock = closingStock; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }

    public static class WastageSummary {
        private String reasonType;
        private Double quantity;
        private Double cost;
        private Integer occurrence;

        public WastageSummary() {}

        public WastageSummary(String reasonType, Double quantity, Double cost, Integer occurrence) {
            this.reasonType = reasonType;
            this.quantity = quantity;
            this.cost = cost;
            this.occurrence = occurrence;
        }

        public String getReasonType() { return reasonType; }
        public void setReasonType(String reasonType) { this.reasonType = reasonType; }
        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }
        public Double getCost() { return cost; }
        public void setCost(Double cost) { this.cost = cost; }
        public Integer getOccurrence() { return occurrence; }
        public void setOccurrence(Integer occurrence) { this.occurrence = occurrence; }
    }
}
