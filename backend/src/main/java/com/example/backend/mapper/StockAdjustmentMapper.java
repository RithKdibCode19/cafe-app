package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.StockAdjustmentRequestDTO;
import com.example.backend.dto.StockAdjustmentResponseDTO;
import com.example.backend.model.StockAdjustmentEntity;

@Mapper(componentModel = "spring", uses = { IngredientMapper.class })
public interface StockAdjustmentMapper {

    StockAdjustmentMapper INSTANCE = Mappers.getMapper(StockAdjustmentMapper.class);

    StockAdjustmentResponseDTO toResponseDTO(StockAdjustmentEntity entity);

    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "adjustmentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    StockAdjustmentEntity toEntity(StockAdjustmentRequestDTO requestDTO);

    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "adjustmentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(StockAdjustmentRequestDTO requestDTO, @MappingTarget StockAdjustmentEntity entity);
}
