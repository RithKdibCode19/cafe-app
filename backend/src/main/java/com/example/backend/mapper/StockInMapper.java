package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.StockInRequestDTO;
import com.example.backend.dto.StockInResponseDTO;
import com.example.backend.model.StockInEntity;

@Mapper(componentModel = "spring", uses = { SupplierMapper.class, IngredientMapper.class })
public interface StockInMapper {

    StockInMapper INSTANCE = Mappers.getMapper(StockInMapper.class);

    StockInResponseDTO toResponseDTO(StockInEntity entity);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "stockId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    StockInEntity toEntity(StockInRequestDTO requestDTO);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "stockId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(StockInRequestDTO requestDTO, @MappingTarget StockInEntity entity);
}
