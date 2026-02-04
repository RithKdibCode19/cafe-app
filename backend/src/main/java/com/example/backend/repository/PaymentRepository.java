package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

        // Find by order ID
        List<PaymentEntity> findByOrderOrderIdAndDeletedAtIsNull(Long orderId);

        // Find by payment method
        List<PaymentEntity> findByMethodAndDeletedAtIsNull(PaymentEntity.PaymentMethod method);

        // Find by payment status
        List<PaymentEntity> findByPaymentStatusAndDeletedAtIsNull(PaymentEntity.PaymentStatus paymentStatus);

        // Find by paid date range
        List<PaymentEntity> findByPaidAtBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

        List<PaymentEntity> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

        // Calculate total payments for period
        @Query("SELECT COALESCE(SUM(p.paidAmount), 0.0) FROM PaymentEntity p " +
                        "WHERE p.paymentStatus = 'PAID' " +
                        "AND p.paidAt >= :startDate AND p.paidAt <= :endDate " +
                        "AND p.deletedAt IS NULL")
        Double calculateTotalPaymentsForPeriod(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Calculate total payments by method for period
        @Query("SELECT COALESCE(SUM(p.paidAmount), 0.0) FROM PaymentEntity p " +
                        "WHERE p.paymentStatus = 'PAID' AND p.method = :method " +
                        "AND p.paidAt >= :startDate AND p.paidAt <= :endDate " +
                        "AND p.deletedAt IS NULL")
        Double calculateTotalPaymentsByMethodForPeriod(@Param("method") PaymentEntity.PaymentMethod method,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
