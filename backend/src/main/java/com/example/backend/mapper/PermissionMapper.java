package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.PermissionRequestDTO;
import com.example.backend.dto.PermissionResponseDTO;
import com.example.backend.model.PermissionEntity;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionResponseDTO toResponseDTO(PermissionEntity entity);

    PermissionEntity toEntity(PermissionRequestDTO requestDTO);

    @Mapping(target = "permissionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(PermissionRequestDTO requestDTO, @MappingTarget PermissionEntity entity);
}
