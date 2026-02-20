package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.VariantRequestDTO;
import com.example.backend.dto.VariantResponseDTO;
import com.example.backend.mapper.VariantMapper;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.model.VariantEntity;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.VariantRepository;


@Service
public class VariantService {

    private final VariantRepository variantRepository;
    private final MenuItemRepository menuItemRepository;
    private final VariantMapper variantMapper;

    public VariantService(VariantRepository variantRepository, MenuItemRepository menuItemRepository, VariantMapper variantMapper) {
        this.variantRepository = variantRepository;
        this.menuItemRepository = menuItemRepository;
        this.variantMapper = variantMapper;
    }

    /**
     * Create a new variant
     */
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "menu-items", allEntries = true)
    public VariantResponseDTO createVariant(VariantRequestDTO request) {
        // 1. Validate menu item exists
        MenuItemEntity menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + request.getMenuItemId()));

        // 2. Map Request DTO → Entity
        VariantEntity variant = variantMapper.toEntity(request);
        variant.setMenuItem(menuItem);
        // Map name to size enum (assuming name represents size)
        variant.setSize(VariantEntity.Size.valueOf(request.getName().toUpperCase()));
        variant.setPrice(menuItem.getBasePrice() + request.getPriceAdjustment());
        variant.setCreatedAt(LocalDateTime.now());
        variant.setUpdatedAt(LocalDateTime.now());

        // 3. Save variant
        VariantEntity savedVariant = variantRepository.save(variant);

        // 4. Map Entity → Response DTO
        return variantMapper.toResponseDTO(savedVariant);
    }

    /**
     * Get all variants
     */
    public List<VariantResponseDTO> getAllVariants() {
        List<VariantEntity> variants = variantRepository.findAll();
        return variants.stream()
                .filter(variant -> variant.getDeletedAt() == null)
                .map(variantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get variant by ID
     */
    public VariantResponseDTO getVariantById(Long id) {
        VariantEntity variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with ID: " + id));

        if (variant.getDeletedAt() != null) {
            throw new RuntimeException("Variant has been deleted");
        }

        return variantMapper.toResponseDTO(variant);
    }

    /**
     * Get variants by menu item ID
     */
    public List<VariantResponseDTO> getVariantsByMenuItemId(Long menuItemId) {
        List<VariantEntity> variants = variantRepository.findByMenuItemMenuItemIdAndDeletedAtIsNull(menuItemId);
        return variants.stream()
                .map(variantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update variant
     */
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "menu-items", allEntries = true)
    public VariantResponseDTO updateVariant(Long id, VariantRequestDTO request) {
        VariantEntity existingVariant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with ID: " + id));

        if (existingVariant.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted variant");
        }

        // Update menu item if changed
        if (!existingVariant.getMenuItem().getMenuItemId().equals(request.getMenuItemId())) {
            MenuItemEntity menuItem = menuItemRepository.findById(request.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            existingVariant.setMenuItem(menuItem);
        }

        // Update other fields
        variantMapper.updateEntityFromDTO(request, existingVariant);
        existingVariant.setSize(VariantEntity.Size.valueOf(request.getName().toUpperCase()));
        existingVariant.setPrice(existingVariant.getMenuItem().getBasePrice() + request.getPriceAdjustment());
        existingVariant.setUpdatedAt(LocalDateTime.now());

        VariantEntity updatedVariant = variantRepository.save(existingVariant);
        return variantMapper.toResponseDTO(updatedVariant);
    }

    /**
     * Soft delete variant
     */
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "menu-items", allEntries = true)
    public void deleteVariant(Long id) {
        VariantEntity variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with ID: " + id));

        variant.setDeletedAt(LocalDateTime.now());
        variant.setUpdatedAt(LocalDateTime.now());
        variantRepository.save(variant);
    }

    /**
     * Get variants by size
     */
    public List<VariantResponseDTO> getVariantsBySize(String size) {
        VariantEntity.Size variantSize = VariantEntity.Size.valueOf(size.toUpperCase());
        List<VariantEntity> variants = variantRepository.findBySizeAndDeletedAtIsNull(variantSize);
        return variants.stream()
                .map(variantMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}