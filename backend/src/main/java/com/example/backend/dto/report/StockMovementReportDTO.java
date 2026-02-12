package com.example.backend.dto.report;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementReportDTO {
    private String startDate;
    private String endDate;
    private List<StockMovementItem> movements;
    private List<String> recommendations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMovementItem {
        private Long ingredientId;
        private String name;
        private String sku;
        private Double openingStock;
        private Double received; // Stock In
        private Double sold; // Stock Out (Sales)
        private Double adjusted; // Manual adjustments
        private Double closingStock;
        private Double wastagePercentage;
        private String unit;
    }
}
