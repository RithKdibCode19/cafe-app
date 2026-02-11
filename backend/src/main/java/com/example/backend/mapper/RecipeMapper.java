package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.backend.dto.RecipeRequestDTO;
import com.example.backend.dto.RecipeResponseDTO;
import com.example.backend.model.RecipeEntity;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {

    @Mapping(source = "menuItem.menuItemId", target = "menuItemId")
    @Mapping(source = "ingredient.ingredientId", target = "ingredientId")
    @Mapping(source = "ingredient.name", target = "ingredientName")
    @Mapping(source = "ingredient.unit", target = "ingredientUnit")
    public abstract RecipeResponseDTO toResponseDTO(RecipeEntity entity);

    @org.mapstruct.AfterMapping
    protected void logMapping(RecipeEntity entity, @org.mapstruct.MappingTarget RecipeResponseDTO dto) {
        if (entity.getIngredient() != null) {
            System.out.println("RecipeMapper: Ingredient Name from Entity = " + entity.getIngredient().getName());
        } else {
            System.out.println("RecipeMapper: Ingredient is NULL");
        }
        System.out.println("RecipeMapper: Mapped DTO Name = " + dto.getIngredientName());
    }

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "recipeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    public abstract RecipeEntity toEntity(RecipeRequestDTO dto);

    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "recipeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    public abstract void updateEntityFromDTO(RecipeRequestDTO dto, @MappingTarget RecipeEntity entity);
}
