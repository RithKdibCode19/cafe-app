package com.example.backend.model;

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
@Table(name = "tbl_pos_order_adjustments")
public class PosOrderAdjustmentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long posOrderAdjustmentId;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType type; // VOID, REFUND

    @Column(nullable = false)
    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    public enum AdjustmentType {
    VOID,
    REFUND
}

    
}
