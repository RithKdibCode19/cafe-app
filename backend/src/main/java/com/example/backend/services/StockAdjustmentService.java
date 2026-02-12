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
import com.example.backend.model.UserEntity;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.StockAdjustmentRepository;
import com.example.backend.repository.UserRepository;


@Service
public class StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final IngredientRepository ingredientRepository;
    private final StockAdjustmentMapper stockAdjustmentMapper;
    private final UserRepository userRepository;

    public StockAdjustmentService(StockAdjustmentRepository stockAdjustmentRepository, IngredientRepository ingredientRepository, StockAdjustmentMapper stockAdjustmentMapper, UserRepository userRepository) {
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.ingredientRepository = ingredientRepository;
        this.stockAdjustmentMapper = stockAdjustmentMapper;
        this.userRepository = userRepository;
    }

    /**
     * Create a new stock adjustment
     */
    @Transactional
    public StockAdjustmentResponseDTO createAdjustment(StockAdjustmentRequestDTO request) {
        // 1. Validate ingredient exists
        IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + request.getIngredientId()));

        UserEntity creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getCreatedBy()));

        // 2. Map Request DTO → Entity
        StockAdjustmentEntity adjustment = stockAdjustmentMapper.toEntity(request);
        adjustment.setIngredient(ingredient);
        adjustment.setReasonType(StockAdjustmentEntity.StockAdjustmentReason.valueOf(request.getReasonType()));
        adjustment.setCreatedBy(creator);
        adjustment.setStatus(StockAdjustmentEntity.AdjustmentStatus.PENDING);
        adjustment.setDate(LocalDateTime.now());
        adjustment.setCreatedAt(LocalDateTime.now());
        adjustment.setUpdatedAt(LocalDateTime.now());

        // 3. Save adjustment record (In PENDING state, inventory is NOT updated yet)
        StockAdjustmentEntity savedAdjustment = stockAdjustmentRepository.save(adjustment);

        // 4. Map into Response
        return stockAdjustmentMapper.toResponseDTO(savedAdjustment);
    }

    /**
     * Approve a stock adjustment with a manager PIN
     */
    @Transactional
    public StockAdjustmentResponseDTO approveAdjustment(Long id, String pinCode) {
        StockAdjustmentEntity adjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment not found with ID: " + id));

        if (adjustment.getStatus() != StockAdjustmentEntity.AdjustmentStatus.PENDING) {
            throw new RuntimeException("Only PENDING adjustments can be approved");
        }

        if (pinCode == null || pinCode.isEmpty()) {
            throw new RuntimeException("Authorization PIN is required for approval");
        }

        UserEntity approver = userRepository.findByPinCodeAndDeletedAtIsNull(pinCode)
                .orElseThrow(() -> new RuntimeException("Invalid Authorization PIN"));

        // Check permission: ORDER_VOID (reusing this for now as it represents managerial power)
        boolean hasPermission = approver.getRole().getPermissions().stream()
                .anyMatch(p -> "ORDER_VOID".equals(p.getCode()));
        
        if (!hasPermission) {
            throw new RuntimeException("User [" + approver.getEmployee().getFullName() + "] is not authorized to approve adjustments");
        }

        // 1. Update Ingredient Stock
        IngredientEntity ingredient = adjustment.getIngredient();
        double newStock = ingredient.getCurrentStock() + adjustment.getQtyChange();
        ingredient.setCurrentStock(newStock);
        ingredient.setUpdatedAt(LocalDateTime.now());
        ingredientRepository.save(ingredient);

        // 2. Update Adjustment Status
        adjustment.setStatus(StockAdjustmentEntity.AdjustmentStatus.APPROVED);
        adjustment.setApprovedBy(approver);
        adjustment.setUpdatedAt(LocalDateTime.now());

        return stockAdjustmentMapper.toResponseDTO(stockAdjustmentRepository.save(adjustment));
    }

    /**
     * Reject a stock adjustment
     */
    @Transactional
    public StockAdjustmentResponseDTO rejectAdjustment(Long id) {
        StockAdjustmentEntity adjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment not found with ID: " + id));

        if (adjustment.getStatus() != StockAdjustmentEntity.AdjustmentStatus.PENDING) {
            throw new RuntimeException("Only PENDING adjustments can be rejected");
        }

        adjustment.setStatus(StockAdjustmentEntity.AdjustmentStatus.REJECTED);
        adjustment.setUpdatedAt(LocalDateTime.now());

        return stockAdjustmentMapper.toResponseDTO(stockAdjustmentRepository.save(adjustment));
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
