package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.BranchStockEntity;

@Repository
public interface BranchStockRepository extends JpaRepository<BranchStockEntity, Long> {
    
    List<BranchStockEntity> findByBranchBranchIdAndDeletedAtIsNull(Long branchId);
    
    Optional<BranchStockEntity> findByBranchBranchIdAndIngredientIngredientIdAndDeletedAtIsNull(Long branchId, Long ingredientId);
    
    List<BranchStockEntity> findByIngredientIngredientIdAndDeletedAtIsNull(Long ingredientId);
}
