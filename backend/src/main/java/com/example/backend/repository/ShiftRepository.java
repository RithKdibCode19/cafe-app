package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.ShiftEntity;

@Repository
public interface ShiftRepository extends JpaRepository<ShiftEntity, Long> {
    java.util.List<ShiftEntity> findAllByDeletedAtIsNull();

    java.util.List<ShiftEntity> findByEmployeeEmployeeIdAndDeletedAtIsNull(Long employeeId);

    java.util.List<ShiftEntity> findByBranchBranchIdAndDeletedAtIsNull(Long branchId);

    java.util.List<ShiftEntity> findByShiftStartBetweenAndDeletedAtIsNull(java.time.LocalDateTime start,
            java.time.LocalDateTime end);
}
