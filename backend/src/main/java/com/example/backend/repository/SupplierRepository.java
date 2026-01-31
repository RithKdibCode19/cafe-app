package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.SupplierEntity;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {

    // Find all active suppliers
    List<SupplierEntity> findAllByDeletedAtIsNull();

    // Find by name
    List<SupplierEntity> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);

    // Find by address
    List<SupplierEntity> findByAddressContainingIgnoreCaseAndDeletedAtIsNull(String address);

    // Find by status
    List<SupplierEntity> findByStatusAndDeletedAtIsNull(SupplierEntity.Status status);

    // Find by supplier ID
    Optional<SupplierEntity> findBySupplierIdAndDeletedAtIsNull(Long supplierId);

    // Check if name exists
    boolean existsByNameAndDeletedAtIsNull(String name);

    // Check if email exists
    boolean existsByEmailAndDeletedAtIsNull(String email);
}
