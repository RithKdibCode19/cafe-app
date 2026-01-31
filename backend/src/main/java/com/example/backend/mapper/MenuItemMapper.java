package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.backend.dto.MenuItemRequestDTO;
import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.model.MenuItemEntity;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface MenuItemMapper {

    MenuItemResponseDTO toResponseDTO(MenuItemEntity entity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "menuItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    MenuItemEntity toEntity(MenuItemRequestDTO requestDTO);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "menuItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(MenuItemRequestDTO requestDTO, @MappingTarget MenuItemEntity entity);
}
