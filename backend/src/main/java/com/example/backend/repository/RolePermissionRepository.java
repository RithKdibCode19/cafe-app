package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.RolePermissionEntity;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {

    // Find all active role permissions
    List<RolePermissionEntity> findAllByDeletedAtIsNull();

    // Find permissions for a role
    List<RolePermissionEntity> findByRoleRoleIdAndDeletedAtIsNull(Long roleId);

    // Find roles for a permission
    List<RolePermissionEntity> findByPermissionPermissionIdAndDeletedAtIsNull(Long permissionId);

    // Find specific role-permission combination
    Optional<RolePermissionEntity> findByRoleRoleIdAndPermissionPermissionIdAndDeletedAtIsNull(Long roleId,
            Long permissionId);

    // Check if role has permission
    boolean existsByRoleRoleIdAndPermissionPermissionIdAndDeletedAtIsNull(Long roleId, Long permissionId);

    // Check if role has permission by code
    @Query("SELECT COUNT(rp) > 0 FROM RolePermissionEntity rp " +
            "WHERE rp.role.roleId = :roleId " +
            "AND rp.permission.code = :permissionCode " +
            "AND rp.deletedAt IS NULL")
    boolean existsByRoleRoleIdAndPermissionCodeAndDeletedAtIsNull(@Param("roleId") Long roleId,
            @Param("permissionCode") String permissionCode);
}
