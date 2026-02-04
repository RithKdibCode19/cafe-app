package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.StockAdjustmentRequestDTO;
import com.example.backend.dto.StockAdjustmentResponseDTO;
import com.example.backend.mapper.StockAdjustmentMapper;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.StockAdjustmentEntity;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.StockAdjustmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final IngredientRepository ingredientRepository;
    private final StockAdjustmentMapper stockAdjustmentMapper;

    /**
     * Create a new stock adjustment
     */
    @Transactional
    public StockAdjustmentResponseDTO createAdjustment(StockAdjustmentRequestDTO request) {
        // 1. Validate ingredient exists
        IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + request.getIngredientId()));

        // 2. Map Request DTO → Entity
        StockAdjustmentEntity adjustment = stockAdjustmentMapper.toEntity(request);
        adjustment.setIngredient(ingredient);
        adjustment.setDate(LocalDateTime.now());
        adjustment.setCreatedAt(LocalDateTime.now());
        adjustment.setUpdatedAt(LocalDateTime.now());

        // 3. Update Ingredient Stock
        // qtyChange can be negative (wastage) or positive (correction)
        double newStock = ingredient.getCurrentStock() + request.getQtyChange();
        ingredient.setCurrentStock(newStock);
        ingredient.setUpdatedAt(LocalDateTime.now());
        ingredientRepository.save(ingredient);

        // 4. Save adjustment record
        StockAdjustmentEntity savedAdjustment = stockAdjustmentRepository.save(adjustment);

        // 5. Map into Response
        return stockAdjustmentMapper.toResponseDTO(savedAdjustment);
    }

    /**
     * Get all adjustments
     */
    public List<StockAdjustmentResponseDTO> getAllAdjustments() {
        return stockAdjustmentRepository.findAllByDeletedAtIsNull().stream()
                .map(stockAdjustmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
