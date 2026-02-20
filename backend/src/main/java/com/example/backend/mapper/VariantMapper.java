package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.VariantResponseDTO;
import com.example.backend.model.VariantEntity;

@Mapper(componentModel = "spring")
public interface VariantMapper {

    VariantMapper INSTANCE = Mappers.getMapper(VariantMapper.class);

    @org.mapstruct.Mapping(target = "name", expression = "java(entity.getSize().name())")
    @org.mapstruct.Mapping(target = "priceAdjustment", expression = "java(entity.getPrice() - entity.getMenuItem().getBasePrice())")
    VariantResponseDTO toResponseDTO(VariantEntity entity);

    com.example.backend.model.VariantEntity toEntity(com.example.backend.dto.VariantRequestDTO dto);

    void updateEntityFromDTO(com.example.backend.dto.VariantRequestDTO dto, @org.mapstruct.MappingTarget com.example.backend.model.VariantEntity entity);
}
