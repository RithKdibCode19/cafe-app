package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.AuditLogRequestDTO;
import com.example.backend.dto.AuditLogResponseDTO;
import com.example.backend.model.AuditLogEntity;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface AuditLogMapper {

    AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);

    AuditLogResponseDTO toResponseDTO(AuditLogEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "logId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    AuditLogEntity toEntity(AuditLogRequestDTO requestDTO);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "logId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(AuditLogRequestDTO requestDTO, @MappingTarget AuditLogEntity entity);
}
