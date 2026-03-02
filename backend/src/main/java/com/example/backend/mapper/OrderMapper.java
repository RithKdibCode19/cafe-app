package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.OrderRequestDTO;
import com.example.backend.dto.OrderResponseDTO;
import com.example.backend.model.OrderEntity;

@Mapper(componentModel = "spring", uses = { BranchMapper.class, UserMapper.class, CustomerMapper.class,
        OrderItemMapper.class })
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "approvedBy.employee.fullName", target = "approvedByName")
    OrderResponseDTO toResponseDTO(OrderEntity entity);

    @Mapping(target = "statusReason", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "deliveryAddress", ignore = true)
    @Mapping(target = "deliveryPhone", ignore = true)
    @Mapping(target = "deliveryNote", ignore = true)
    @Mapping(target = "deliveryFee", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "customerPhone", ignore = true)
    OrderEntity toEntity(OrderRequestDTO requestDTO);

    @Mapping(target = "statusReason", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "deliveryAddress", ignore = true)
    @Mapping(target = "deliveryPhone", ignore = true)
    @Mapping(target = "deliveryNote", ignore = true)
    @Mapping(target = "deliveryFee", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "customerPhone", ignore = true)
    void updateEntityFromDTO(OrderRequestDTO requestDTO, @MappingTarget OrderEntity entity);
}
