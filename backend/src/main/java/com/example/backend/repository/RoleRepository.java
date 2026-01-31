package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    // Find all active roles
    List<RoleEntity> findAllByDeletedAtIsNull();

    // Find by role name
    Optional<RoleEntity> findByRoleNameAndDeletedAtIsNull(String roleName);

    // Check if role name exists
    boolean existsByRoleNameAndDeletedAtIsNull(String roleName);

    // Search by role name
    List<RoleEntity> findByRoleNameContainingIgnoreCaseAndDeletedAtIsNull(String roleName);
}
