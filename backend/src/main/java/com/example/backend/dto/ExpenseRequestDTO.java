package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseRequestDTO {
    private String title;
    private String description;
    private Double amount;
    private LocalDate date;
    private String category;
    private Long branchId;
    private Long recordedById;
}
