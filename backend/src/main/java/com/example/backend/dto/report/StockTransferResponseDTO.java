package com.example.backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferResponseDTO {
    private Long transferId;
    private String fromBranchName;
    private String toBranchName;
    private String ingredientName;
    private String unit;
    private Double quantity;
    private String transferredByName;
    private LocalDateTime transferDate;
}
