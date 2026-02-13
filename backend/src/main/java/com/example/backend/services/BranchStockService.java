package com.example.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.BranchEntity;
import com.example.backend.model.BranchStockEntity;
import com.example.backend.model.IngredientEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.BranchStockRepository;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.mapper.BranchStockMapper;
import com.example.backend.dto.BranchStockResponseDTO;

@Service
public class BranchStockService {

    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchStockMapper branchStockMapper;

    public BranchStockService(BranchStockRepository branchStockRepository,
                             BranchRepository branchRepository,
                             IngredientRepository ingredientRepository,
                             BranchStockMapper branchStockMapper) {
        this.branchStockRepository = branchStockRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
        this.branchStockMapper = branchStockMapper;
    }

    public boolean isStockAvailable(Long branchId, Long ingredientId, Double requiredAmount) {
        BranchStockEntity stock = findOrCreateStock(branchId, ingredientId);
        return stock.getCurrentStock() >= requiredAmount;
    }

    public Double getCurrentStock(Long branchId, Long ingredientId) {
        return findOrCreateStock(branchId, ingredientId).getCurrentStock();
    }

    public boolean isLowStock(Long branchId, Long ingredientId) {
        BranchStockEntity stock = findOrCreateStock(branchId, ingredientId);
        Double reorderLevel = stock.getReorderLevel() != null ? stock.getReorderLevel() : stock.getIngredient().getReorderLevel();
        return stock.getCurrentStock() <= (reorderLevel != null ? reorderLevel : 0.0);
    }

    @Transactional
    @com.example.backend.security.IsolateByBranch
    public void adjustStock(Long branchId, Long ingredientId, Double delta) {
        BranchStockEntity stock = findOrCreateStock(branchId, ingredientId);
        stock.setCurrentStock(stock.getCurrentStock() + delta);
        branchStockRepository.save(stock);

        // Also update global stock for overview
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        ingredient.setCurrentStock((ingredient.getCurrentStock() != null ? ingredient.getCurrentStock() : 0.0) + delta);
        ingredientRepository.save(ingredient);
    }

    @Transactional
    @com.example.backend.security.IsolateByBranch("fromBranchId")
    public void transferStock(Long fromBranchId, Long toBranchId, Long ingredientId, Double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive");
        
        BranchStockEntity sourceStock = branchStockRepository.findByBranchBranchIdAndIngredientIngredientIdAndDeletedAtIsNull(fromBranchId, ingredientId)
                .orElseThrow(() -> new RuntimeException("Source branch does not have this ingredient"));
        
        if (sourceStock.getCurrentStock() < amount) {
            throw new RuntimeException("Insufficient stock in source branch");
        }

        // Deduct from source
        sourceStock.setCurrentStock(sourceStock.getCurrentStock() - amount);
        branchStockRepository.save(sourceStock);

        // Add to destination
        BranchStockEntity destStock = findOrCreateStock(toBranchId, ingredientId);
        destStock.setCurrentStock(destStock.getCurrentStock() + amount);
        branchStockRepository.save(destStock);

        // Note: Global stock doesn't change during internal transfers
    }

    private BranchStockEntity findOrCreateStock(Long branchId, Long ingredientId) {
        return branchStockRepository.findByBranchBranchIdAndIngredientIngredientIdAndDeletedAtIsNull(branchId, ingredientId)
                .orElseGet(() -> {
                    BranchEntity branch = branchRepository.findById(branchId)
                            .orElseThrow(() -> new RuntimeException("Branch not found"));
                    IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                            .orElseThrow(() -> new RuntimeException("Ingredient not found"));
                    
                    return BranchStockEntity.builder()
                            .branch(branch)
                            .ingredient(ingredient)
                            .currentStock(0.0)
                            .reorderLevel(ingredient.getReorderLevel())
                            .build();
                });
    }

    @com.example.backend.security.IsolateByBranch
    public List<BranchStockResponseDTO> getBranchInventory(Long branchId) {
        return branchStockRepository.findByBranchBranchIdAndDeletedAtIsNull(branchId)
                .stream()
                .map(branchStockMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
