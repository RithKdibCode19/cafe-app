package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.backend.dto.CategoryRequestDTO;
import com.example.backend.dto.CategoryResponseDTO;
import com.example.backend.model.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)

    CategoryEntity toEntity(CategoryRequestDTO requestDTO);
    CategoryResponseDTO toResponseDTO(CategoryEntity entity);
    void updateEntityFromDTO(CategoryRequestDTO requestDTO, @MappingTarget CategoryEntity entity);
}
