package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.EmployeeRequestDTO;
import com.example.backend.dto.EmployeeResponseDTO;
import com.example.backend.model.EmployeeEntity;

@Mapper(componentModel = "spring", uses = { BranchMapper.class })
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeResponseDTO toResponseDTO(EmployeeEntity entity);

    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    EmployeeEntity toEntity(EmployeeRequestDTO requestDTO);

    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(EmployeeRequestDTO requestDTO, @MappingTarget EmployeeEntity entity);
}
