package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CategoryRequestDTO;
import com.example.backend.dto.CategoryResponseDTO;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.model.CategoryEntity;
import com.example.backend.repository.CategoryRepository;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;

        this.categoryMapper = categoryMapper;
    }
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        CategoryEntity categoryEntity = categoryMapper.toEntity(request);
        if (request.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            categoryEntity.setParent(parent);
        }
        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(savedCategory);
    }   
    public List<CategoryResponseDTO> getAllCategories() {
        // Fetch only root categories (categories with no parent)
        List<CategoryEntity> categoryEntities = categoryRepository.findRoots();
        return categoryEntities.stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }
    public CategoryResponseDTO getCategoryById(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toResponseDTO(categoryEntity);
    }
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryMapper.updateEntityFromDTO(request, categoryEntity);
        
        if (request.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            categoryEntity.setParent(parent);
        } else {
             categoryEntity.setParent(null);
        }

        CategoryEntity updatedCategory = categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(updatedCategory);
    }
    public void deleteCategory(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryEntity.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(categoryEntity);
    }
}