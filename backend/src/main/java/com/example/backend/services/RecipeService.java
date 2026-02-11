package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.RecipeRequestDTO;
import com.example.backend.dto.RecipeResponseDTO;
import com.example.backend.mapper.RecipeMapper;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.model.RecipeEntity;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.RecipeRepository;


@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository, MenuItemRepository menuItemRepository, IngredientRepository ingredientRepository, RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.menuItemRepository = menuItemRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeMapper = recipeMapper;
    }

    @Transactional
    public RecipeResponseDTO createRecipe(RecipeRequestDTO request) {
        MenuItemEntity menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu Item not found"));

        IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        RecipeEntity recipe = recipeMapper.toEntity(request);
        recipe.setMenuItem(menuItem);
        recipe.setIngredient(ingredient);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());

        RecipeEntity savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toResponseDTO(savedRecipe);
    }

    public List<RecipeResponseDTO> getRecipesByMenuItemId(Long menuItemId) {
        return recipeRepository.findByMenuItemMenuItemId(menuItemId).stream()
                .map(recipeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }
}
