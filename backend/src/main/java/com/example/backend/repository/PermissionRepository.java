package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    // Find all active permissions
    List<PermissionEntity> findAllByDeletedAtIsNull();

    // Find by permission code
    Optional<PermissionEntity> findByCodeAndDeletedAtIsNull(String code);

    // Check if permission code exists
    boolean existsByCodeAndDeletedAtIsNull(String code);

    // Search by description
    List<PermissionEntity> findByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String description);
}
