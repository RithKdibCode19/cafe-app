package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.PosOrderAdjustmentRequestDTO;
import com.example.backend.dto.PosOrderAdjustmentResponseDTO;
import com.example.backend.mapper.PosOrderAdjustmentMapper;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.PosOrderAdjustmentEntity;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PosOrderAdjustmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PosOrderAdjustmentService {

    private final PosOrderAdjustmentRepository posOrderAdjustmentRepository;
    private final OrderRepository orderRepository;
    private final PosOrderAdjustmentMapper posOrderAdjustmentMapper;
    private final OrderService orderService;

    /**
     * Create new order adjustment
     */
    @Transactional
    public PosOrderAdjustmentResponseDTO createAdjustment(PosOrderAdjustmentRequestDTO request) {
        // 1. Validate order exists
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.getOrderId()));

        // 2. Validate order can be adjusted
        if (order.getStatus() == OrderEntity.OrderStatus.REFUND || order.getStatus() == OrderEntity.OrderStatus.VOID) {
            throw new RuntimeException("Cannot adjust order that is already refunded or voided");
        }

        // 3. Map Request DTO → Entity
        PosOrderAdjustmentEntity adjustment = posOrderAdjustmentMapper.toEntity(request);
        adjustment.setOrder(order);
        adjustment.setType(PosOrderAdjustmentEntity.AdjustmentType.valueOf(request.getType()));
        adjustment.setCreatedAt(LocalDateTime.now());
        adjustment.setUpdatedAt(LocalDateTime.now());

        // 4. Save adjustment
        PosOrderAdjustmentEntity savedAdjustment = posOrderAdjustmentRepository.save(adjustment);

        // 5. Update order status based on adjustment type if approved
        if (request.getApprovedBy() != null) {
            updateOrderStatusForAdjustment(order, adjustment.getType());
        }

        // 6. Map Entity → Response DTO
        return posOrderAdjustmentMapper.toResponseDTO(savedAdjustment);
    }

    /**
     * Get all adjustments
     */
    public List<PosOrderAdjustmentResponseDTO> getAllAdjustments() {
        List<PosOrderAdjustmentEntity> adjustments = posOrderAdjustmentRepository.findAllByDeletedAtIsNull();
        return adjustments.stream()
                .map(posOrderAdjustmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get adjustment by ID
     */
    public PosOrderAdjustmentResponseDTO getAdjustmentById(Long id) {
        PosOrderAdjustmentEntity adjustment = posOrderAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment not found with ID: " + id));

        if (adjustment.getDeletedAt() != null) {
            throw new RuntimeException("Adjustment has been deleted");
        }

        return posOrderAdjustmentMapper.toResponseDTO(adjustment);
    }

    /**
     * Get adjustments by order ID
     */
    public List<PosOrderAdjustmentResponseDTO> getAdjustmentsByOrderId(Long orderId) {
        List<PosOrderAdjustmentEntity> adjustments = posOrderAdjustmentRepository
                .findByOrderOrderIdAndDeletedAtIsNull(orderId);
        return adjustments.stream()
                .map(posOrderAdjustmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get adjustments by type
     */
    public List<PosOrderAdjustmentResponseDTO> getAdjustmentsByType(String type) {
        PosOrderAdjustmentEntity.AdjustmentType adjustmentType = PosOrderAdjustmentEntity.AdjustmentType.valueOf(type);
        List<PosOrderAdjustmentEntity> adjustments = posOrderAdjustmentRepository
                .findByTypeAndDeletedAtIsNull(adjustmentType);
        return adjustments.stream()
                .map(posOrderAdjustmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending adjustments (not yet approved)
     */
    public List<PosOrderAdjustmentResponseDTO> getPendingAdjustments() {
        List<PosOrderAdjustmentEntity> adjustments = posOrderAdjustmentRepository
                .findByApprovedByIsNullAndDeletedAtIsNull();
        return adjustments.stream()
                .map(posOrderAdjustmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approve adjustment
     */
    @Transactional
    public PosOrderAdjustmentResponseDTO approveAdjustment(Long adjustmentId, Long approvedBy) {
        PosOrderAdjustmentEntity adjustment = posOrderAdjustmentRepository.findById(adjustmentId)
                .orElseThrow(() -> new RuntimeException("Adjustment not found with ID: " + adjustmentId));

        if (adjustment.getApprovedBy() != null) {
            throw new RuntimeException("Adjustment is already approved");
        }

        adjustment.setApprovedBy(approvedBy);
        adjustment.setUpdatedAt(LocalDateTime.now());

        PosOrderAdjustmentEntity approvedAdjustment = posOrderAdjustmentRepository.save(adjustment);

        // Update order status
        updateOrderStatusForAdjustment(adjustment.getOrder(), adjustment.getType());

        return posOrderAdjustmentMapper.toResponseDTO(approvedAdjustment);
    }

    /**
     * Soft delete adjustment
     */
    @Transactional
    public void deleteAdjustment(Long id) {
        PosOrderAdjustmentEntity adjustment = posOrderAdjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment not found with ID: " + id));

        if (adjustment.getApprovedBy() != null) {
            throw new RuntimeException("Cannot delete approved adjustment");
        }

        adjustment.setDeletedAt(LocalDateTime.now());
        adjustment.setUpdatedAt(LocalDateTime.now());
        posOrderAdjustmentRepository.save(adjustment);
    }

    // Private helper methods

    private void updateOrderStatusForAdjustment(OrderEntity order, PosOrderAdjustmentEntity.AdjustmentType type) {
        switch (type) {
            case VOID:
                order.setStatus(OrderEntity.OrderStatus.VOID);
                break;
            case REFUND:
                order.setStatus(OrderEntity.OrderStatus.REFUND);
                break;
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // Restore inventory when order is voided or refunded
        orderService.restoreInventoryForOrder(order);
    }
}