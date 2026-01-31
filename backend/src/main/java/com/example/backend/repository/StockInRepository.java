package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.StockInEntity;

@Repository
public interface StockInRepository extends JpaRepository<StockInEntity, Long> {

    // Find all active stock in records
    List<StockInEntity> findAllByDeletedAtIsNull();

    // Find by supplier
    List<StockInEntity> findBySupplierSupplierIdAndDeletedAtIsNull(Long supplierId);

    // Find by ingredient
    List<StockInEntity> findByIngredientIngredientIdAndDeletedAtIsNull(Long ingredientId);

    // Find by received date range
    List<StockInEntity> findByReceivedDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    // Find by invoice number
    List<StockInEntity> findByInvoiceNoContainingIgnoreCaseAndDeletedAtIsNull(String invoiceNo);

    // Find by received by user
    List<StockInEntity> findByReceivedByAndDeletedAtIsNull(Long receivedBy);
}
