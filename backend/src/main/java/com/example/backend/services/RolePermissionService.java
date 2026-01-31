package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.RolePermissionRequestDTO;
import com.example.backend.dto.RolePermissionResponseDTO;
import com.example.backend.mapper.RolePermissionMapper;
import com.example.backend.model.PermissionEntity;
import com.example.backend.model.RoleEntity;
import com.example.backend.model.RolePermissionEntity;
import com.example.backend.repository.PermissionRepository;
import com.example.backend.repository.RolePermissionRepository;
import com.example.backend.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionMapper rolePermissionMapper;

    /**
     * Assign permission to role
     */
    @Transactional
    public RolePermissionResponseDTO assignPermissionToRole(RolePermissionRequestDTO request) {
        // 1. Validate role exists
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + request.getRoleId()));

        // 2. Validate permission exists
        PermissionEntity permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + request.getPermissionId()));

        // 3. Check if assignment already exists
        if (rolePermissionRepository.existsByRoleRoleIdAndPermissionPermissionIdAndDeletedAtIsNull(
                request.getRoleId(), request.getPermissionId())) {
            throw new RuntimeException("Permission is already assigned to this role");
        }

        // 4. Map Request DTO → Entity
        RolePermissionEntity rolePermission = rolePermissionMapper.toEntity(request);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setCreatedAt(LocalDateTime.now());
        rolePermission.setUpdatedAt(LocalDateTime.now());

        // 5. Save role permission
        RolePermissionEntity savedRolePermission = rolePermissionRepository.save(rolePermission);

        // 6. Map Entity → Response DTO
        return rolePermissionMapper.toResponseDTO(savedRolePermission);
    }

    /**
     * Get all role permissions
     */
    public List<RolePermissionResponseDTO> getAllRolePermissions() {
        List<RolePermissionEntity> rolePermissions = rolePermissionRepository.findAllByDeletedAtIsNull();
        return rolePermissions.stream()
                .map(rolePermissionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get permissions for a specific role
     */
    public List<RolePermissionResponseDTO> getPermissionsByRoleId(Long roleId) {
        List<RolePermissionEntity> rolePermissions = rolePermissionRepository
                .findByRoleRoleIdAndDeletedAtIsNull(roleId);
        return rolePermissions.stream()
                .map(rolePermissionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get roles for a specific permission
     */
    public List<RolePermissionResponseDTO> getRolesByPermissionId(Long permissionId) {
        List<RolePermissionEntity> rolePermissions = rolePermissionRepository
                .findByPermissionPermissionIdAndDeletedAtIsNull(permissionId);
        return rolePermissions.stream()
                .map(rolePermissionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Remove permission from role
     */
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        RolePermissionEntity rolePermission = rolePermissionRepository
                .findByRoleRoleIdAndPermissionPermissionIdAndDeletedAtIsNull(roleId, permissionId)
                .orElseThrow(() -> new RuntimeException(
                        "Role permission not found for role ID: " + roleId + " and permission ID: " + permissionId));

        // Soft delete
        rolePermission.setDeletedAt(LocalDateTime.now());
        rolePermission.setUpdatedAt(LocalDateTime.now());
        rolePermissionRepository.save(rolePermission);
    }

    /**
     * Remove all permissions from role
     */
    @Transactional
    public void removeAllPermissionsFromRole(Long roleId) {
        List<RolePermissionEntity> rolePermissions = rolePermissionRepository
                .findByRoleRoleIdAndDeletedAtIsNull(roleId);

        rolePermissions.forEach(rp -> {
            rp.setDeletedAt(LocalDateTime.now());
            rp.setUpdatedAt(LocalDateTime.now());
        });

        rolePermissionRepository.saveAll(rolePermissions);
    }

    /**
     * Check if role has specific permission
     */
    public boolean roleHasPermission(Long roleId, Long permissionId) {
        return rolePermissionRepository.existsByRoleRoleIdAndPermissionPermissionIdAndDeletedAtIsNull(
                roleId, permissionId);
    }

    /**
     * Check if role has permission by code
     */
    public boolean roleHasPermissionByCode(Long roleId, String permissionCode) {
        return rolePermissionRepository.existsByRoleRoleIdAndPermissionCodeAndDeletedAtIsNull(
                roleId, permissionCode);
    }
}