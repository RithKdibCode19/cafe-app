package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.backend.dto.AttendanceRequestDTO;
import com.example.backend.dto.AttendanceResponseDTO;
import com.example.backend.model.AttendanceEntity;

@Mapper(componentModel = "spring", uses = { EmployeeMapper.class })
public interface AttendanceMapper {

    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

    AttendanceResponseDTO toResponseDTO(AttendanceEntity entity);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "attendanceId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    AttendanceEntity toEntity(AttendanceRequestDTO requestDTO);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "attendanceId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(AttendanceRequestDTO requestDTO, @MappingTarget AttendanceEntity entity);
}
