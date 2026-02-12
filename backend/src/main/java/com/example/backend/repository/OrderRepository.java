package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

        // Find by branch
        List<OrderEntity> findByBranchBranchIdAndDeletedAtIsNull(Long branchId);

        // Find by status
        List<OrderEntity> findByStatusAndDeletedAtIsNull(OrderEntity.OrderStatus status);

        // Find by cashier user
        List<OrderEntity> findByCashierUserUserIdAndDeletedAtIsNull(Long cashierUserId);

        // Find by customer
        List<OrderEntity> findByCustomerCustomerIdAndDeletedAtIsNull(Long customerId);

        // Find by order type
        List<OrderEntity> findByOrderTypeAndDeletedAtIsNull(OrderEntity.OrderType orderType);

        // Find by date range
        List<OrderEntity> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

        // Find by branch and date range
        List<OrderEntity> findByBranchBranchIdAndCreatedAtBetweenAndDeletedAtIsNull(
                        Long branchId, LocalDateTime startDate, LocalDateTime endDate);

        List<OrderEntity> findByCashierUserUserIdAndCreatedAtBetweenAndDeletedAtIsNull(
                        Long userId, LocalDateTime startDate, LocalDateTime endDate);

        // Find by branch and status
        List<OrderEntity> findByBranchBranchIdAndStatusAndDeletedAtIsNull(Long branchId,
                        OrderEntity.OrderStatus status);

        // Search by order number
        List<OrderEntity> findByOrderNoContainingIgnoreCaseAndDeletedAtIsNull(String orderNo);

        // Find by exact order number
        OrderEntity findByOrderNoAndDeletedAtIsNull(String orderNo);

        // Count orders by status
        long countByStatusAndDeletedAtIsNull(OrderEntity.OrderStatus status);

        // Count today's orders for branch
        @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.branch.branchId = :branchId " +
                        "AND o.createdAt >= :startDate AND o.createdAt <= :endDate " +
                        "AND o.deletedAt IS NULL")
        long countTodayOrdersForBranch(@Param("branchId") Long branchId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Get recent orders (last N orders)
        @Query("SELECT o FROM OrderEntity o WHERE o.deletedAt IS NULL " +
                        "ORDER BY o.createdAt DESC LIMIT :limit")
        List<OrderEntity> findRecentOrders(@Param("limit") int limit);

        // Get pending orders for a branch
        @Query("SELECT o FROM OrderEntity o WHERE o.branch.branchId = :branchId " +
                        "AND o.status = 'PENDING' AND o.deletedAt IS NULL " +
                        "ORDER BY o.createdAt ASC")
        List<OrderEntity> findPendingOrdersForBranch(@Param("branchId") Long branchId);

        // Calculate total sales for date range
        @Query("SELECT COALESCE(SUM(oi.qty * oi.unitPrice), 0.0) FROM OrderEntity o " +
                        "JOIN o.items oi WHERE o.status IN ('PAID', 'PREPARING', 'READY', 'COMPLETED') " +
                        "AND o.createdAt >= :startDate AND o.createdAt <= :endDate " +
                        "AND o.deletedAt IS NULL")
        Double calculateTotalSalesForPeriod(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Calculate total sales for branch and date range
        @Query("SELECT COALESCE(SUM(oi.qty * oi.unitPrice), 0.0) FROM OrderEntity o " +
                        "JOIN o.items oi WHERE o.branch.branchId = :branchId " +
                        "AND o.status IN ('PAID', 'PREPARING', 'READY', 'COMPLETED') " +
                        "AND o.createdAt >= :startDate AND o.createdAt <= :endDate " +
                        "AND o.deletedAt IS NULL")
        Double calculateTotalSalesForBranchAndPeriod(@Param("branchId") Long branchId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Get orders with items (fetch join for performance)
        @Query("SELECT DISTINCT o FROM OrderEntity o " +
                        "LEFT JOIN FETCH o.items " +
                        "LEFT JOIN FETCH o.branch " +
                        "LEFT JOIN FETCH o.cashierUser " +
                        "LEFT JOIN FETCH o.customer " +
                        "WHERE o.deletedAt IS NULL " +
                        "ORDER BY o.createdAt DESC")
        List<OrderEntity> findAllWithDetails();

        // Check if order number exists
        boolean existsByOrderNoAndDeletedAtIsNull(String orderNo);

        @Query("SELECT DISTINCT o FROM OrderEntity o " +
                        "LEFT JOIN FETCH o.items " +
                        "WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate " +
                        "AND o.deletedAt IS NULL")
        List<OrderEntity> findByCreatedAtBetweenWithItems(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
        // ... existing methods ...
        @org.springframework.data.jpa.repository.Query("SELECT oi.menuItem.name, COUNT(oi) as total " +
                        "FROM OrderEntity o " +
                        "JOIN o.items oi " +
                        "WHERE o.branch.branchId = :branchId " +
                        "AND o.status IN ('PAID', 'COMPLETED', 'READY', 'PREPARING') " +
                        "AND o.deletedAt IS NULL " +
                        "GROUP BY oi.menuItem.name " +
                        "ORDER BY total DESC")
        java.util.List<Object[]> findTopMenuItemForBranch(@org.springframework.data.repository.query.Param("branchId") Long branchId, org.springframework.data.domain.Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.branch.branchId = :branchId " +
                        "AND o.status IN ('PAID', 'COMPLETED', 'READY', 'PREPARING') " +
                        "AND o.createdAt >= :startDate AND o.createdAt <= :endDate " +
                        "AND o.deletedAt IS NULL")
        long countOrdersForBranchAndPeriod(@org.springframework.data.repository.query.Param("branchId") Long branchId,
                        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
                        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

        // --- Pagination Support ---

        // Find all active orders (paginated)
        Page<OrderEntity> findByDeletedAtIsNull(Pageable pageable);

        // Find by status (paginated)
        Page<OrderEntity> findByStatusAndDeletedAtIsNull(OrderEntity.OrderStatus status, Pageable pageable);

        // Search by order number (paginated)
        Page<OrderEntity> findByOrderNoContainingIgnoreCaseAndDeletedAtIsNull(String orderNo, Pageable pageable);
}
