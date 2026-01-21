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

}
