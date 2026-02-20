package com.example.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.BranchEntity;
import com.example.backend.model.BranchStockEntity;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.StockTransferEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.BranchStockRepository;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.StockTransferRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.mapper.BranchStockMapper;
import com.example.backend.dto.BranchStockResponseDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

@Service
public class BranchStockService {

    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchStockMapper branchStockMapper;
    private final StockTransferRepository stockTransferRepository;
    private final UserRepository userRepository;

    public BranchStockService(BranchStockRepository branchStockRepository,
                             BranchRepository branchRepository,
                             IngredientRepository ingredientRepository,
                             BranchStockMapper branchStockMapper,
                             StockTransferRepository stockTransferRepository,
                             UserRepository userRepository) {
        this.branchStockRepository = branchStockRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
        this.branchStockMapper = branchStockMapper;
        this.stockTransferRepository = stockTransferRepository;
        this.userRepository = userRepository;
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

        // Record the transfer transaction
        recordTransferLog(fromBranchId, toBranchId, ingredientId, amount);

        // Note: Global stock doesn't change during internal transfers
    }

    private void recordTransferLog(Long fromBranchId, Long toBranchId, Long ingredientId, Double amount) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                UserEntity user = userRepository.findById(userDetails.getId()).orElse(null);
                BranchEntity fromBranch = branchRepository.findById(fromBranchId).orElse(null);
                BranchEntity toBranch = branchRepository.findById(toBranchId).orElse(null);
                IngredientEntity ingredient = ingredientRepository.findById(ingredientId).orElse(null);

                if (user != null && fromBranch != null && toBranch != null && ingredient != null) {
                    StockTransferEntity transfer = StockTransferEntity.builder()
                            .fromBranch(fromBranch)
                            .toBranch(toBranch)
                            .ingredient(ingredient)
                            .quantity(amount)
                            .transferredBy(user)
                            .transferDate(LocalDateTime.now())
                            .build();
                    stockTransferRepository.save(transfer);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the transfer if logging fails
            System.err.println("Failed to record stock transfer log: " + e.getMessage());
        }
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
