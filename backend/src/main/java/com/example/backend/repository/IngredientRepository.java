package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.IngredientEntity;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

    // Find all active ingredients
    List<IngredientEntity> findAllByDeletedAtIsNull();

    // Find by name
    List<IngredientEntity> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);

    // Find by unit
    List<IngredientEntity> findByUnitAndDeletedAtIsNull(IngredientEntity.IngredientUnit unit);

    // Find by SKU
    Optional<IngredientEntity> findBySkuAndDeletedAtIsNull(String sku);

    // Check if name exists
    boolean existsByNameAndDeletedAtIsNull(String name);

    // Check if SKU exists
    boolean existsBySkuAndDeletedAtIsNull(String sku);

    // Find low stock ingredients (custom query)
    @Query("SELECT i FROM IngredientEntity i WHERE i.currentStock < i.reorderLevel AND i.deletedAt IS NULL")
    List<IngredientEntity> findLowStockIngredients();

    // Find by ingredient ID
    Optional<IngredientEntity> findByIngredientIdAndDeletedAtIsNull(Long ingredientId);
}
