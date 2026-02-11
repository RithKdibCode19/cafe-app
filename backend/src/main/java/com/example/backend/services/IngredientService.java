package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.IngredientRequestDTO;
import com.example.backend.dto.IngredientResponseDTO;
import com.example.backend.mapper.IngredientMapper;
import com.example.backend.model.IngredientEntity;
import com.example.backend.repository.IngredientRepository;


@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public IngredientService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    /**
     * Create a new ingredient
     */
    @Transactional
    public IngredientResponseDTO createIngredient(IngredientRequestDTO request) {
        // Check if ingredient name already exists
        if (ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new RuntimeException("Ingredient with name '" + request.getName() + "' already exists");
        }

        // Map Request DTO → Entity
        IngredientEntity ingredient = ingredientMapper.toEntity(request);
        ingredient.setCreatedAt(LocalDateTime.now());
        ingredient.setUpdatedAt(LocalDateTime.now());

        // Save ingredient
        IngredientEntity savedIngredient = ingredientRepository.save(ingredient);

        // Map Entity → Response DTO
        return ingredientMapper.toResponseDTO(savedIngredient);
    }

    /**
     * Get all ingredients
     */
    public List<IngredientResponseDTO> getAllIngredients() {
        List<IngredientEntity> ingredients = ingredientRepository.findAllByDeletedAtIsNull();
        return ingredients.stream()
                .map(ingredientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get ingredient by ID
     */
    public IngredientResponseDTO getIngredientById(Long id) {
        IngredientEntity ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + id));

        if (ingredient.getDeletedAt() != null) {
            throw new RuntimeException("Ingredient has been deleted");
        }

        return ingredientMapper.toResponseDTO(ingredient);
    }

    /**
     * Get ingredients by SKU
     */
    public IngredientResponseDTO getIngredientBySku(String sku) {
        IngredientEntity ingredient = ingredientRepository
                .findBySkuAndDeletedAtIsNull(sku)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with SKU: " + sku));
        return ingredientMapper.toResponseDTO(ingredient);
    }

    /**
     * Search ingredients by name
     */
    public List<IngredientResponseDTO> searchIngredientsByName(String name) {
        List<IngredientEntity> ingredients = ingredientRepository
                .findByNameContainingIgnoreCaseAndDeletedAtIsNull(name);
        return ingredients.stream()
                .map(ingredientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update ingredient
     */
    @Transactional
    public IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO request) {
        IngredientEntity existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + id));

        if (existingIngredient.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted ingredient");
        }

        // Check if new name conflicts with existing ones
        if (!existingIngredient.getName().equals(request.getName()) &&
                ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new RuntimeException("Ingredient with name '" + request.getName() + "' already exists");
        }

        // Update fields
        ingredientMapper.updateEntityFromDTO(request, existingIngredient);
        existingIngredient.setUpdatedAt(LocalDateTime.now());

        IngredientEntity updatedIngredient = ingredientRepository.save(existingIngredient);
        return ingredientMapper.toResponseDTO(updatedIngredient);
    }

    /**
     * Soft delete ingredient
     */
    @Transactional
    public void deleteIngredient(Long id) {
        IngredientEntity ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + id));

        // Check if ingredient is being used in stock or recipes
        // if (stockInRepository.existsByIngredientIngredientId(id)) {
        // throw new RuntimeException("Cannot delete ingredient that has stock
        // records");
        // }

        ingredient.setDeletedAt(LocalDateTime.now());
        ingredient.setUpdatedAt(LocalDateTime.now());
        ingredientRepository.save(ingredient);
    }

    /**
     * Get ingredients by unit
     */
    public List<IngredientResponseDTO> getIngredientsByUnit(IngredientEntity.IngredientUnit unit) {
        List<IngredientEntity> ingredients = ingredientRepository
                .findByUnitAndDeletedAtIsNull(unit);
        return ingredients.stream()
                .map(ingredientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get low stock ingredients
     */
    public List<IngredientResponseDTO> getLowStockIngredients() {
        List<IngredientEntity> ingredients = ingredientRepository
                .findLowStockIngredients();
        return ingredients.stream()
                .map(ingredientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}