package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.StockInRequestDTO;
import com.example.backend.dto.StockInResponseDTO;
import com.example.backend.mapper.StockInMapper;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.StockInEntity;
import com.example.backend.model.SupplierEntity;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.StockInRepository;
import com.example.backend.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInService {

    private final StockInRepository stockInRepository;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;
    private final StockInMapper stockInMapper;

    /**
     * Record new stock receipt
     */
    @Transactional
    public StockInResponseDTO recordStockIn(StockInRequestDTO request) {
        // 1. Validate supplier exists
        SupplierEntity supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + request.getSupplierId()));

        // 2. Validate ingredient exists
        IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found with ID: " + request.getIngredientId()));

        // 3. Map Request DTO → Entity
        StockInEntity stockIn = stockInMapper.toEntity(request);
        stockIn.setSupplier(supplier);
        stockIn.setIngredient(ingredient);
        stockIn.setReceivedDate(request.getReceivedDate() != null ? request.getReceivedDate() : LocalDateTime.now());
        stockIn.setCreatedAt(LocalDateTime.now());
        stockIn.setUpdatedAt(LocalDateTime.now());

        // 4. Calculate total cost if not provided
        if (stockIn.getTotalCost() == null) {
            stockIn.setTotalCost(stockIn.getQtyIn() * stockIn.getUnitCost());
        }

        // 5. Save stock in record
        StockInEntity savedStockIn = stockInRepository.save(stockIn);

        // 6. Map Entity → Response DTO
        return stockInMapper.toResponseDTO(savedStockIn);
    }

    /**
     * Get all stock in records
     */
    public List<StockInResponseDTO> getAllStockInRecords() {
        List<StockInEntity> stockInRecords = stockInRepository.findAllByDeletedAtIsNull();
        return stockInRecords.stream()
                .map(stockInMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get stock in record by ID
     */
    public StockInResponseDTO getStockInById(Long id) {
        StockInEntity stockIn = stockInRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock in record not found with ID: " + id));

        if (stockIn.getDeletedAt() != null) {
            throw new RuntimeException("Stock in record has been deleted");
        }

        return stockInMapper.toResponseDTO(stockIn);
    }

    /**
     * Get stock in records by supplier
     */
    public List<StockInResponseDTO> getStockInBySupplier(Long supplierId) {
        List<StockInEntity> stockInRecords = stockInRepository
                .findBySupplierSupplierIdAndDeletedAtIsNull(supplierId);
        return stockInRecords.stream()
                .map(stockInMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get stock in records by ingredient
     */
    public List<StockInResponseDTO> getStockInByIngredient(Long ingredientId) {
        List<StockInEntity> stockInRecords = stockInRepository
                .findByIngredientIngredientIdAndDeletedAtIsNull(ingredientId);
        return stockInRecords.stream()
                .map(stockInMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get stock in records by date range
     */
    public List<StockInResponseDTO> getStockInByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockInEntity> stockInRecords = stockInRepository
                .findByReceivedDateBetweenAndDeletedAtIsNull(startDate, endDate);
        return stockInRecords.stream()
                .map(stockInMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update stock in record
     */
    @Transactional
    public StockInResponseDTO updateStockIn(Long id, StockInRequestDTO request) {
        StockInEntity existingStockIn = stockInRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock in record not found with ID: " + id));

        if (existingStockIn.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted stock in record");
        }

        // Update relationships if changed
        if (!existingStockIn.getSupplier().getSupplierId().equals(request.getSupplierId())) {
            SupplierEntity supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            existingStockIn.setSupplier(supplier);
        }

        if (!existingStockIn.getIngredient().getIngredientId().equals(request.getIngredientId())) {
            IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));
            existingStockIn.setIngredient(ingredient);
        }

        // Update other fields
        stockInMapper.updateEntityFromDTO(request, existingStockIn);
        existingStockIn.setUpdatedAt(LocalDateTime.now());

        // Recalculate total cost if needed
        if (existingStockIn.getTotalCost() == null) {
            existingStockIn.setTotalCost(existingStockIn.getQtyIn() * existingStockIn.getUnitCost());
        }

        StockInEntity updatedStockIn = stockInRepository.save(existingStockIn);
        return stockInMapper.toResponseDTO(updatedStockIn);
    }

    /**
     * Soft delete stock in record
     */
    @Transactional
    public void deleteStockIn(Long id) {
        StockInEntity stockIn = stockInRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock in record not found with ID: " + id));

        stockIn.setDeletedAt(LocalDateTime.now());
        stockIn.setUpdatedAt(LocalDateTime.now());
        stockInRepository.save(stockIn);
    }

    /**
     * Get stock in records by invoice number
     */
    public List<StockInResponseDTO> getStockInByInvoiceNo(String invoiceNo) {
        List<StockInEntity> stockInRecords = stockInRepository
                .findByInvoiceNoContainingIgnoreCaseAndDeletedAtIsNull(invoiceNo);
        return stockInRecords.stream()
                .map(stockInMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}