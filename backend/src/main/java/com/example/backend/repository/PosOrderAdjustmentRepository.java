package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.PosOrderAdjustmentEntity;

@Repository
public interface PosOrderAdjustmentRepository extends JpaRepository<PosOrderAdjustmentEntity, Long> {

    // Find all active adjustments
    List<PosOrderAdjustmentEntity> findAllByDeletedAtIsNull();

    // Find by order ID
    List<PosOrderAdjustmentEntity> findByOrderOrderIdAndDeletedAtIsNull(Long orderId);

    // Find by adjustment type
    List<PosOrderAdjustmentEntity> findByTypeAndDeletedAtIsNull(PosOrderAdjustmentEntity.AdjustmentType type);

    // Find pending adjustments (not approved)
    List<PosOrderAdjustmentEntity> findByApprovedByIsNullAndDeletedAtIsNull();

    // Find approved adjustments
    List<PosOrderAdjustmentEntity> findByApprovedByIsNotNullAndDeletedAtIsNull();

    // Find by requested by
    List<PosOrderAdjustmentEntity> findByRequestedByAndDeletedAtIsNull(Long requestedBy);

    // Find by approved by
    List<PosOrderAdjustmentEntity> findByApprovedByAndDeletedAtIsNull(Long approvedBy);
}
