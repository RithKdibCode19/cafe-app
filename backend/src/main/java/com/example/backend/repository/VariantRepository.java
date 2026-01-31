package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.VariantEntity;

@Repository
public interface VariantRepository extends JpaRepository<VariantEntity, Long> {

    // Find by menu item ID
    List<VariantEntity> findByMenuItemMenuItemIdAndDeletedAtIsNull(Long menuItemId);

    // Find by size
    List<VariantEntity> findBySizeAndDeletedAtIsNull(VariantEntity.Size size);
}
