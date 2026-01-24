package com.example.backend.services;

import org.springframework.stereotype.Service;

import com.example.backend.repository.CategoryRepository;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.model.CategoryEntity;
import com.example.backend.dto.CategoryRequestDTO;
import com.example.backend.dto.CategoryResponseDTO;
import java.util.List;
import java.time.LocalDateTime;


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
        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(savedCategory);
    }   
    public List<CategoryResponseDTO> getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
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
        CategoryEntity updatedCategory = categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(updatedCategory);
    }
    public void deleteCategory(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryEntity.setIsDeleted(true);
        categoryEntity.setDeletedAt(LocalDateTime.now());
        categoryEntity.setDeletedBy(null);
        categoryRepository.save(categoryEntity);
    }
}