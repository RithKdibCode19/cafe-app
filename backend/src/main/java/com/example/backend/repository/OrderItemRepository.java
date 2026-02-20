package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.OrderItemEntity;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT oi2.menuItem.menuItemId, COUNT(oi2) as freq " +
            "FROM OrderItemEntity oi1 " +
            "JOIN oi1.order o " +
            "JOIN o.items oi2 " +
            "WHERE oi1.menuItem.menuItemId = :menuItemId " +
            "AND oi2.menuItem.menuItemId <> :menuItemId " +
            "AND o.status IN ('PAID', 'COMPLETED') " +
            "GROUP BY oi2.menuItem.menuItemId " +
            "ORDER BY freq DESC")
    java.util.List<Object[]> findFrequentlyBoughtTogether(@org.springframework.data.repository.query.Param("menuItemId") Long menuItemId, org.springframework.data.domain.Pageable pageable);
}
