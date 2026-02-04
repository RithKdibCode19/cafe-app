package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.RoleRequestDTO;
import com.example.backend.dto.RoleResponseDTO;
import com.example.backend.model.RoleEntity;
import com.example.backend.mapper.PermissionMapper;

@Mapper(componentModel = "spring", uses = { PermissionMapper.class })
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleResponseDTO toResponseDTO(RoleEntity entity);

    @Mapping(target = "permissions", ignore = true) // Handled in Service
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    RoleEntity toEntity(RoleRequestDTO requestDTO);

    @Mapping(target = "permissions", ignore = true) // Handled in Service
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(RoleRequestDTO requestDTO, @MappingTarget RoleEntity entity);
}
