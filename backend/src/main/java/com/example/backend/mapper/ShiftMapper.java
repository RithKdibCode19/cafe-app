package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.ShiftRequestDTO;
import com.example.backend.dto.ShiftResponseDTO;
import com.example.backend.model.ShiftEntity;

@Mapper(componentModel = "spring", uses = { EmployeeMapper.class, BranchMapper.class })
public interface ShiftMapper {

    ShiftMapper INSTANCE = Mappers.getMapper(ShiftMapper.class);

    @Mapping(source = "startTime", target = "shiftStart")
    @Mapping(source = "endTime", target = "shiftEnd")
    ShiftResponseDTO toResponseDTO(ShiftEntity entity);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "shiftId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(source = "shiftStart", target = "startTime")
    @Mapping(source = "shiftEnd", target = "endTime")
    @Mapping(target = "startCash", ignore = true)
    @Mapping(target = "endCash", ignore = true)
    ShiftEntity toEntity(ShiftRequestDTO requestDTO);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "shiftId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(source = "shiftStart", target = "startTime")
    @Mapping(source = "shiftEnd", target = "endTime")
    @Mapping(target = "startCash", ignore = true)
    @Mapping(target = "endCash", ignore = true)
    void updateEntityFromDTO(ShiftRequestDTO requestDTO, @MappingTarget ShiftEntity entity);
}
