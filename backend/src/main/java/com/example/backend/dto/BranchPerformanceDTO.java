package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPerformanceDTO {
    private Long branchId;
    private String branchName;
    private Double totalSales;
    private Long orderCount;
    private Double avgTransactionValue;
    private String topMenuItem;
}
