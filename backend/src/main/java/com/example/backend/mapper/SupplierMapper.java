package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.SupplierRequestDTO;
import com.example.backend.dto.SupplierResponseDTO;
import com.example.backend.model.SupplierEntity;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierMapper INSTANCE = Mappers.getMapper(SupplierMapper.class);

    SupplierResponseDTO toResponseDTO(SupplierEntity entity);

    SupplierEntity toEntity(SupplierRequestDTO requestDTO);

    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(SupplierRequestDTO requestDTO, @MappingTarget SupplierEntity entity);
}
