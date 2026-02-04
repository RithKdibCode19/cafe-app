package com.example.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_stock_adjustments")
public class StockAdjustmentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_id")
    private Long adjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    @Column(name = "qty_change", nullable = false)
    private Double qtyChange; // can be + or -

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false)
    private StockAdjustmentReason reasonType;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private LocalDateTime date;

    public enum StockAdjustmentReason {
        WASTAGE,
        DAMAGE,
        EXPIRED,
        COUNT_CORRECTION
    }

    // Manual Getters/Setters
    public Long getAdjustmentId() { return adjustmentId; }
    public void setAdjustmentId(Long adjustmentId) { this.adjustmentId = adjustmentId; }

    public IngredientEntity getIngredient() { return ingredient; }
    public void setIngredient(IngredientEntity ingredient) { this.ingredient = ingredient; }

    public Double getQtyChange() { return qtyChange; }
    public void setQtyChange(Double qtyChange) { this.qtyChange = qtyChange; }

    public StockAdjustmentReason getReasonType() { return reasonType; }
    public void setReasonType(StockAdjustmentReason reasonType) { this.reasonType = reasonType; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
