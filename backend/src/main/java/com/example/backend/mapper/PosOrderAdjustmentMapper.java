package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.PosOrderAdjustmentRequestDTO;
import com.example.backend.dto.PosOrderAdjustmentResponseDTO;
import com.example.backend.model.PosOrderAdjustmentEntity;

@Mapper(componentModel = "spring")
public interface PosOrderAdjustmentMapper {

    PosOrderAdjustmentMapper INSTANCE = Mappers.getMapper(PosOrderAdjustmentMapper.class);

    @Mapping(target = "orderId", source = "order.orderId")
    PosOrderAdjustmentResponseDTO toResponseDTO(PosOrderAdjustmentEntity entity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "posOrderAdjustmentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    PosOrderAdjustmentEntity toEntity(PosOrderAdjustmentRequestDTO requestDTO);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "posOrderAdjustmentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(PosOrderAdjustmentRequestDTO requestDTO, @MappingTarget PosOrderAdjustmentEntity entity);
}
