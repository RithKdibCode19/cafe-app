package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.RolePermissionRequestDTO;
import com.example.backend.dto.RolePermissionResponseDTO;
import com.example.backend.model.RolePermissionEntity;

@Mapper(componentModel = "spring", uses = { RoleMapper.class, PermissionMapper.class })
public interface RolePermissionMapper {

    RolePermissionMapper INSTANCE = Mappers.getMapper(RolePermissionMapper.class);

    RolePermissionResponseDTO toResponseDTO(RolePermissionEntity entity);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "rolePermissionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    RolePermissionEntity toEntity(RolePermissionRequestDTO requestDTO);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "rolePermissionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(RolePermissionRequestDTO requestDTO, @MappingTarget RolePermissionEntity entity);
}
