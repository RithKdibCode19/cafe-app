package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.SupplierRequestDTO;
import com.example.backend.dto.SupplierResponseDTO;
import com.example.backend.mapper.SupplierMapper;
import com.example.backend.model.SupplierEntity;
import com.example.backend.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    /**
     * Create a new supplier
     */
    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO request) {
        // Check if supplier name already exists
        if (supplierRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new RuntimeException("Supplier with name '" + request.getName() + "' already exists");
        }

        // Check if email already exists
        if (request.getEmail() != null &&
                supplierRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new RuntimeException("Supplier with email '" + request.getEmail() + "' already exists");
        }

        // Map Request DTO → Entity
        SupplierEntity supplier = supplierMapper.toEntity(request);
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());

        // Save supplier
        SupplierEntity savedSupplier = supplierRepository.save(supplier);

        // Map Entity → Response DTO
        return supplierMapper.toResponseDTO(savedSupplier);
    }

    /**
     * Get all suppliers
     */
    public List<SupplierResponseDTO> getAllSuppliers() {
        List<SupplierEntity> suppliers = supplierRepository.findAllByDeletedAtIsNull();
        return suppliers.stream()
                .map(supplierMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get supplier by ID
     */
    public SupplierResponseDTO getSupplierById(Long id) {
        SupplierEntity supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        if (supplier.getDeletedAt() != null) {
            throw new RuntimeException("Supplier has been deleted");
        }

        return supplierMapper.toResponseDTO(supplier);
    }

    /**
     * Search suppliers by name
     */
    public List<SupplierResponseDTO> searchSuppliersByName(String name) {
        List<SupplierEntity> suppliers = supplierRepository
                .findByNameContainingIgnoreCaseAndDeletedAtIsNull(name);
        return suppliers.stream()
                .map(supplierMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search suppliers by address
     */
    public List<SupplierResponseDTO> searchSuppliersByAddress(String address) {
        List<SupplierEntity> suppliers = supplierRepository
                .findByAddressContainingIgnoreCaseAndDeletedAtIsNull(address);
        return suppliers.stream()
                .map(supplierMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active suppliers
     */
    public List<SupplierResponseDTO> getActiveSuppliers() {
        List<SupplierEntity> suppliers = supplierRepository
                .findByStatusAndDeletedAtIsNull(SupplierEntity.Status.ACTIVE);
        return suppliers.stream()
                .map(supplierMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update supplier
     */
    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO request) {
        SupplierEntity existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        if (existingSupplier.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted supplier");
        }

        // Check if new name conflicts with existing ones
        if (!existingSupplier.getName().equals(request.getName()) &&
                supplierRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new RuntimeException("Supplier with name '" + request.getName() + "' already exists");
        }

        // Check if new email conflicts with existing ones
        if (request.getEmail() != null &&
                !request.getEmail().equals(existingSupplier.getEmail()) &&
                supplierRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new RuntimeException("Supplier with email '" + request.getEmail() + "' already exists");
        }

        // Update fields
        supplierMapper.updateEntityFromDTO(request, existingSupplier);
        existingSupplier.setUpdatedAt(LocalDateTime.now());

        SupplierEntity updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toResponseDTO(updatedSupplier);
    }

    /**
     * Soft delete supplier
     */
    @Transactional
    public void deleteSupplier(Long id) {
        SupplierEntity supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        // Check if supplier has active stock records
        // if (stockInRepository.existsBySupplierSupplierId(id)) {
        // throw new RuntimeException("Cannot delete supplier that has stock records");
        // }

        supplier.setDeletedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }

    /**
     * Activate/Deactivate supplier
     */
    @Transactional
    public SupplierResponseDTO toggleSupplierStatus(Long id) {
        SupplierEntity supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        if (supplier.getDeletedAt() != null) {
            throw new RuntimeException("Cannot modify deleted supplier");
        }

        // Toggle between ACTIVE and INACTIVE status
        SupplierEntity.Status newStatus = supplier.getStatus() == SupplierEntity.Status.ACTIVE
                ? SupplierEntity.Status.INACTIVE
                : SupplierEntity.Status.ACTIVE;
        supplier.setStatus(newStatus);
        supplier.setUpdatedAt(LocalDateTime.now());

        SupplierEntity updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(updatedSupplier);
    }
}