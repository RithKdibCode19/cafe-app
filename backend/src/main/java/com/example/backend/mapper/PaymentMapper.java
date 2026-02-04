package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.PaymentRequestDTO;
import com.example.backend.dto.PaymentResponseDTO;
import com.example.backend.model.PaymentEntity;

@Mapper(componentModel = "spring", uses = { OrderMapper.class })
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "orderId", source = "order.orderId")
    PaymentResponseDTO toResponseDTO(PaymentEntity entity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    PaymentEntity toEntity(PaymentRequestDTO requestDTO);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(PaymentRequestDTO requestDTO, @MappingTarget PaymentEntity entity);
}
