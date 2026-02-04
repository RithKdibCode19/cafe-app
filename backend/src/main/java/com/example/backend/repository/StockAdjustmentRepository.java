package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.StockAdjustmentEntity;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustmentEntity, Long> {
    java.util.List<StockAdjustmentEntity> findAllByDeletedAtIsNull();

    java.util.List<StockAdjustmentEntity> findByCreatedAtBetweenAndDeletedAtIsNull(java.time.LocalDateTime start,
            java.time.LocalDateTime end);
}
