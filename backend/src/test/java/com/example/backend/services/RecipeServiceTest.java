package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.RecipeRequestDTO;
import com.example.backend.dto.RecipeResponseDTO;
import com.example.backend.mapper.RecipeMapper;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.model.RecipeEntity;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.RecipeRepository;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService service;

    @Test
    void createRecipe_Success() {
        RecipeRequestDTO req = new RecipeRequestDTO();
        req.setMenuItemId(10L);
        req.setIngredientId(5L);
        req.setQuantityNeeded(0.1);

        MenuItemEntity menu = new MenuItemEntity();
        menu.setMenuItemId(10L);

        IngredientEntity ing = new IngredientEntity();
        ing.setIngredientId(5L);

        RecipeEntity recipe = new RecipeEntity();

        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(menu));
        when(ingredientRepository.findById(5L)).thenReturn(Optional.of(ing));
        when(recipeMapper.toEntity(req)).thenReturn(recipe);
        when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(recipe);
        when(recipeMapper.toResponseDTO(recipe)).thenReturn(new RecipeResponseDTO());

        assertDoesNotThrow(() -> service.createRecipe(req));

        verify(recipeRepository).save(recipe);
    }
}
