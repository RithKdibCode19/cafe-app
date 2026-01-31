package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.PermissionRequestDTO;
import com.example.backend.dto.PermissionResponseDTO;
import com.example.backend.mapper.PermissionMapper;
import com.example.backend.model.PermissionEntity;
import com.example.backend.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Create a new permission
     */
    @Transactional
    public PermissionResponseDTO createPermission(PermissionRequestDTO request) {
        // Generate permission code from name
        String permissionCode = generatePermissionCode(request.getPermissionName());

        // Check if permission code already exists
        if (permissionRepository.existsByCodeAndDeletedAtIsNull(permissionCode)) {
            throw new RuntimeException("Permission with code '" + permissionCode + "' already exists");
        }

        // Map Request DTO → Entity
        PermissionEntity permission = permissionMapper.toEntity(request);
        permission.setCode(permissionCode);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());

        // Save permission
        PermissionEntity savedPermission = permissionRepository.save(permission);

        // Map Entity → Response DTO
        return permissionMapper.toResponseDTO(savedPermission);
    }

    /**
     * Get all permissions
     */
    public List<PermissionResponseDTO> getAllPermissions() {
        List<PermissionEntity> permissions = permissionRepository.findAllByDeletedAtIsNull();
        return permissions.stream()
                .map(permissionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get permission by ID
     */
    public PermissionResponseDTO getPermissionById(Long id) {
        PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + id));

        if (permission.getDeletedAt() != null) {
            throw new RuntimeException("Permission has been deleted");
        }

        return permissionMapper.toResponseDTO(permission);
    }

    /**
     * Get permission by code
     */
    public PermissionResponseDTO getPermissionByCode(String code) {
        PermissionEntity permission = permissionRepository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new RuntimeException("Permission not found with code: " + code));

        return permissionMapper.toResponseDTO(permission);
    }

    /**
     * Update permission
     */
    @Transactional
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request) {
        PermissionEntity existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + id));

        if (existingPermission.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted permission");
        }

        // Generate new permission code
        String newPermissionCode = generatePermissionCode(request.getPermissionName());

        // Check if new permission code conflicts with existing ones
        if (!existingPermission.getCode().equals(newPermissionCode) &&
                permissionRepository.existsByCodeAndDeletedAtIsNull(newPermissionCode)) {
            throw new RuntimeException("Permission with code '" + newPermissionCode + "' already exists");
        }

        // Update fields
        permissionMapper.updateEntityFromDTO(request, existingPermission);
        existingPermission.setCode(newPermissionCode);
        existingPermission.setUpdatedAt(LocalDateTime.now());

        PermissionEntity updatedPermission = permissionRepository.save(existingPermission);
        return permissionMapper.toResponseDTO(updatedPermission);
    }

    /**
     * Soft delete permission
     */
    @Transactional
    public void deletePermission(Long id) {
        PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + id));

        // Check if permission is being used by roles (you might want to add this check)
        // if (rolePermissionRepository.existsByPermissionPermissionId(id)) {
        // throw new RuntimeException("Cannot delete permission that is assigned to
        // roles");
        // }

        permission.setDeletedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        permissionRepository.save(permission);
    }

    /**
     * Search permissions by name
     */
    public List<PermissionResponseDTO> searchPermissionsByName(String permissionName) {
        List<PermissionEntity> permissions = permissionRepository
                .findByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(permissionName);
        return permissions.stream()
                .map(permissionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private String generatePermissionCode(String permissionName) {
        return permissionName.toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");
    }
}