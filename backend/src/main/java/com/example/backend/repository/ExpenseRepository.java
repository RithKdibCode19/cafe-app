package com.example.backend.repository;

import com.example.backend.model.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findByBranchBranchIdAndDeletedAtIsNull(Long branchId);

    List<ExpenseEntity> findByDateBetweenAndDeletedAtIsNull(LocalDate start, LocalDate end);

    List<ExpenseEntity> findByBranchBranchIdAndDateBetweenAndDeletedAtIsNull(Long branchId, LocalDate start,
            LocalDate end);
}
