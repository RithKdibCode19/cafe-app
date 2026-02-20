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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private UserEntity approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_status", nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private AdjustmentStatus status = AdjustmentStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    public enum StockAdjustmentReason {
        WASTAGE,
        DAMAGE,
        EXPIRED,
        COUNT_CORRECTION
    }

    public enum AdjustmentStatus {
        PENDING,
        APPROVED,
        REJECTED
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

    public UserEntity getApprovedBy() { return approvedBy; }
    public void setApprovedBy(UserEntity approvedBy) { this.approvedBy = approvedBy; }

    public UserEntity getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserEntity createdBy) { this.createdBy = createdBy; }

    public AdjustmentStatus getStatus() { return status; }
    public void setStatus(AdjustmentStatus status) { this.status = status; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public BranchEntity getBranch() { return branch; }
    public void setBranch(BranchEntity branch) { this.branch = branch; }
}
