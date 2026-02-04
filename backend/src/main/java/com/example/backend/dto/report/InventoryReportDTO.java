package com.example.backend.dto.report;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportDTO {
    private String startDate;
    private String endDate;
    private Double totalInventoryValue;
    private Integer lowStockItemsCount;
    private Double totalWasteCost;
    private List<StockMovement> movements;
    private List<WastageSummary> wastageSummary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMovement {
        private Long ingredientId;
        private String name;
        private Double openingStock;
        private Double stockIn;
        private Double consumed;
        private Double adjustments;
        private Double closingStock;
        private String unit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WastageSummary {
        private String reasonType;
        private Double quantity;
        private Double cost;
        private Integer occurrence;
    }
}
