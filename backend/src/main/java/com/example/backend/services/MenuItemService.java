package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.MenuItemRequestDTO;
import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.mapper.MenuItemMapper;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MenuItemRepository;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemMapper menuItemMapper;
    private final com.example.backend.repository.RecipeRepository recipeRepository;
    private final BranchStockService branchStockService;

    public MenuItemService(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository,
            MenuItemMapper menuItemMapper, com.example.backend.repository.RecipeRepository recipeRepository,
            BranchStockService branchStockService) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemMapper = menuItemMapper;
        this.recipeRepository = recipeRepository;
        this.branchStockService = branchStockService;
    }

    public List<MenuItemResponseDTO> getAllMenuItems(Long branchId) {
        List<MenuItemEntity> entities = menuItemRepository.findAllActive();
        List<MenuItemResponseDTO> dtos = entities.stream()
                .map(menuItemMapper::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());

        if (branchId != null) {
            for (MenuItemResponseDTO dto : dtos) {
                // If it's already manually unavailable, leave it
                if (dto.getIsAvailable() == null || !dto.getIsAvailable()) continue;

                // Check stock for all required ingredients in recipes
                List<com.example.backend.model.RecipeEntity> recipes = recipeRepository
                        .findByMenuItemMenuItemId(dto.getMenuItemId());
                
                boolean isSoldOut = false;
                boolean isLow = false;

                for (com.example.backend.model.RecipeEntity recipe : recipes) {
                    Long ingredientId = recipe.getIngredient().getIngredientId();
                    Double qtyNeeded = recipe.getQuantityNeeded();

                    if (!branchStockService.isStockAvailable(branchId, ingredientId, qtyNeeded)) {
                        isSoldOut = true;
                        break;
                    }

                    if (branchStockService.isLowStock(branchId, ingredientId)) {
                        isLow = true;
                    }
                }

                if (isSoldOut) {
                    dto.setIsAvailable(false);
                } else if (isLow) {
                    dto.setLowStock(true);
                }
            }
        }
        return dtos;
    }

    public MenuItemResponseDTO getMenuItemById(Long id) {
        MenuItemEntity entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        return menuItemMapper.toResponseDTO(entity);
    }

    @CacheEvict(value = "menu-items", allEntries = true)
    @Transactional
    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO request) {
        MenuItemEntity entity = menuItemMapper.toEntity(request);
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        entity.setCategory(category);
        MenuItemEntity saved = menuItemRepository.save(entity);
        return menuItemMapper.toResponseDTO(saved);
    }

    @CacheEvict(value = "menu-items", allEntries = true)
    @Transactional
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemRequestDTO request) {
        MenuItemEntity entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        menuItemMapper.updateEntityFromDTO(request, entity);
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            entity.setCategory(category);
        }
        MenuItemEntity updated = menuItemRepository.save(entity);
        return menuItemMapper.toResponseDTO(updated);
    }

    @CacheEvict(value = "menu-items", allEntries = true)
    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItemEntity entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        entity.setDeletedAt(LocalDateTime.now());
        menuItemRepository.save(entity);
    }
}
