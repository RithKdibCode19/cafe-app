package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.OrderItemRequestDTO;
import com.example.backend.dto.OrderItemResponseDTO;
import com.example.backend.model.OrderItemEntity;

@Mapper(componentModel = "spring", uses = { MenuItemMapper.class, VariantMapper.class })
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItemResponseDTO toResponseDTO(OrderItemEntity entity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "variant", ignore = true)
    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    OrderItemEntity toEntity(OrderItemRequestDTO requestDTO);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "variant", ignore = true)
    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(OrderItemRequestDTO requestDTO, @MappingTarget OrderItemEntity entity);
}
