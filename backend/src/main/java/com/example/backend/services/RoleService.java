package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.RoleRequestDTO;
import com.example.backend.dto.RoleResponseDTO;
import com.example.backend.mapper.RoleMapper;
import com.example.backend.model.RoleEntity;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.PermissionRepository;
import com.example.backend.model.PermissionEntity;
import java.util.HashSet;
import java.util.Set;


@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Create a new role
     */
    @Transactional
    public RoleResponseDTO createRole(RoleRequestDTO request) {
        // Check if role name already exists
        if (roleRepository.existsByRoleNameAndDeletedAtIsNull(request.getRoleName())) {
            throw new RuntimeException("Role with name '" + request.getRoleName() + "' already exists");
        }

        // Map Request DTO -> Entity
        RoleEntity role = roleMapper.toEntity(request);

        // Handle Permissions
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<PermissionEntity> permissions = new HashSet<>(
                    permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(permissions);
        }

        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        // Save role
        RoleEntity savedRole = roleRepository.save(role);

        // Map Entity → Response DTO
        return roleMapper.toResponseDTO(savedRole);
    }

    /**
     * Get all roles
     */
    public List<RoleResponseDTO> getAllRoles() {
        List<RoleEntity> roles = roleRepository.findAllByDeletedAtIsNull();
        return roles.stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get role by ID
     */
    public RoleResponseDTO getRoleById(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        if (role.getDeletedAt() != null) {
            throw new RuntimeException("Role has been deleted");
        }

        return roleMapper.toResponseDTO(role);
    }

    /**
     * Get role by name
     */
    public RoleResponseDTO getRoleByName(String roleName) {
        RoleEntity role = roleRepository.findByRoleNameAndDeletedAtIsNull(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

        return roleMapper.toResponseDTO(role);
    }

    /**
     * Update role
     */
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO request) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        if (existingRole.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted role");
        }

        // Check if new role name conflicts with existing ones
        if (!existingRole.getRoleName().equals(request.getRoleName()) &&
                roleRepository.existsByRoleNameAndDeletedAtIsNull(request.getRoleName())) {
            throw new RuntimeException("Role with name '" + request.getRoleName() + "' already exists");
        }

        // Update fields
        roleMapper.updateEntityFromDTO(request, existingRole);

        // Update Permissions
        if (request.getPermissionIds() != null) {
            Set<PermissionEntity> permissions = new HashSet<>(
                    permissionRepository.findAllById(request.getPermissionIds()));
            existingRole.setPermissions(permissions);
        }
        existingRole.setUpdatedAt(LocalDateTime.now());

        RoleEntity updatedRole = roleRepository.save(existingRole);
        return roleMapper.toResponseDTO(updatedRole);
    }

    /**
     * Soft delete role
     */
    @Transactional
    public void deleteRole(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        // Check if role is being used by users (you might want to add this check)
        // if (userRepository.existsByRoleRoleId(id)) {
        // throw new RuntimeException("Cannot delete role that is assigned to users");
        // }

        role.setDeletedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.save(role);
    }

    /**
     * Search roles by name
     */
    public List<RoleResponseDTO> searchRolesByName(String roleName) {
        List<RoleEntity> roles = roleRepository.findByRoleNameContainingIgnoreCaseAndDeletedAtIsNull(roleName);
        return roles.stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}