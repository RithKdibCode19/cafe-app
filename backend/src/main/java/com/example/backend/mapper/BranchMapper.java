package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.backend.dto.BranchRequestDTO;
import com.example.backend.dto.BranchResponseDTO;
import com.example.backend.model.BranchEntity;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchResponseDTO toResponseDTO(BranchEntity entity);
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    BranchEntity toEntity(BranchRequestDTO requestDTO);

    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(BranchRequestDTO requestDTO, @MappingTarget BranchEntity entity);
}
