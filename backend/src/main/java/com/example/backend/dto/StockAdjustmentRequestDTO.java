package com.example.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequestDTO {

    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    @NotNull(message = "Quantity change is required")
    private Double qtyChange;

    @NotBlank(message = "Reason type is required")
    private String reasonType;

    private String note;

    private Long approvedBy;

    @NotNull(message = "Created by is required")
    private Long createdBy;

    @NotNull(message = "Date is required")
    private LocalDateTime date;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    // Manual Getters/Setters
    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }

    public Double getQtyChange() { return qtyChange; }
    public void setQtyChange(Double qtyChange) { this.qtyChange = qtyChange; }

    public String getReasonType() { return reasonType; }
    public void setReasonType(String reasonType) { this.reasonType = reasonType; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
}
