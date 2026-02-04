package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.backend.dto.RecipeRequestDTO;
import com.example.backend.dto.RecipeResponseDTO;
import com.example.backend.model.RecipeEntity;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(source = "menuItem.menuItemId", target = "menuItemId")
    @Mapping(source = "ingredient.ingredientId", target = "ingredientId")
    @Mapping(source = "ingredient.name", target = "ingredientName")
    @Mapping(source = "ingredient.unit", target = "ingredientUnit")
    RecipeResponseDTO toResponseDTO(RecipeEntity entity);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "recipeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    RecipeEntity toEntity(RecipeRequestDTO dto);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "recipeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromDTO(RecipeRequestDTO dto, @MappingTarget RecipeEntity entity);
}
