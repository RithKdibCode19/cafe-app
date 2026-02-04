package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseResponseDTO {
    private Long expenseId;
    private String title;
    private String description;
    private Double amount;
    private LocalDate date;
    private String category;
    private Long branchId;
    private String branchName;
    private String recordedByName;
}
