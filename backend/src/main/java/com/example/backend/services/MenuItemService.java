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

    public MenuItemService(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository,
            MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemMapper = menuItemMapper;
    }

    public List<MenuItemResponseDTO> getAllMenuItems() {
        List<MenuItemEntity> entities = menuItemRepository.findAllActive();
        return entities.stream()
                .map(menuItemMapper::toResponseDTO)
                .toList();
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
