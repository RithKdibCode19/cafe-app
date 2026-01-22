package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.VariantRequestDTO;
import com.example.backend.dto.VariantResponseDTO;
import com.example.backend.model.VariantEntity;

@Mapper(componentModel = "spring")
public interface VariantMapper {

    VariantMapper INSTANCE = Mappers.getMapper(VariantMapper.class);

    VariantResponseDTO toResponseDTO(VariantEntity entity);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "variantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    VariantEntity toEntity(VariantRequestDTO requestDTO);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "variantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(VariantRequestDTO requestDTO, @MappingTarget VariantEntity entity);
}
