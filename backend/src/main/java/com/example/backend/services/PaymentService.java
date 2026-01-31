package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.PaymentRequestDTO;
import com.example.backend.dto.PaymentResponseDTO;
import com.example.backend.mapper.PaymentMapper;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.PaymentEntity;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    /**
     * Process a new payment
     */
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // 1. Validate order exists
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + request.getOrderId()));

        // 2. Validate order is in correct status
        if (order.getStatus() != OrderEntity.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot process payment for order with status: " + order.getStatus());
        }

        // 3. Map Request DTO → Entity
        PaymentEntity payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setMethod(PaymentEntity.PaymentMethod.valueOf(request.getMethod()));
        payment.setPaymentStatus(PaymentEntity.PaymentStatus.valueOf(request.getPaymentStatus()));
        payment.setPaidAt(request.getPaidAt() != null ? request.getPaidAt() : LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        // 4. Save payment
        PaymentEntity savedPayment = paymentRepository.save(payment);

        // 5. Update order status if payment is successful
        if (PaymentEntity.PaymentStatus.valueOf(request.getPaymentStatus()) == PaymentEntity.PaymentStatus.PAID) {
            order.setStatus(OrderEntity.OrderStatus.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        // 6. Map Entity → Response DTO
        return paymentMapper.toResponseDTO(savedPayment);
    }

    /**
     * Get all payments
     */
    public List<PaymentResponseDTO> getAllPayments() {
        List<PaymentEntity> payments = paymentRepository.findAll();
        return payments.stream()
                .filter(payment -> payment.getDeletedAt() == null)
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payment by ID
     */
    public PaymentResponseDTO getPaymentById(Long id) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        if (payment.getDeletedAt() != null) {
            throw new RuntimeException("Payment has been deleted");
        }

        return paymentMapper.toResponseDTO(payment);
    }

    /**
     * Get payments by order ID
     */
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        List<PaymentEntity> payments = paymentRepository.findByOrderOrderIdAndDeletedAtIsNull(orderId);
        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by payment method
     */
    public List<PaymentResponseDTO> getPaymentsByMethod(String method) {
        PaymentEntity.PaymentMethod paymentMethod = PaymentEntity.PaymentMethod.valueOf(method);
        List<PaymentEntity> payments = paymentRepository.findByMethodAndDeletedAtIsNull(paymentMethod);
        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by status
     */
    public List<PaymentResponseDTO> getPaymentsByStatus(String status) {
        PaymentEntity.PaymentStatus paymentStatus = PaymentEntity.PaymentStatus.valueOf(status);
        List<PaymentEntity> payments = paymentRepository.findByPaymentStatusAndDeletedAtIsNull(paymentStatus);
        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by date range
     */
    public List<PaymentResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<PaymentEntity> payments = paymentRepository.findByPaidAtBetweenAndDeletedAtIsNull(startDate, endDate);
        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Refund a payment
     */
    @Transactional
    public PaymentResponseDTO refundPayment(Long paymentId, String refundReason) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getPaymentStatus() != PaymentEntity.PaymentStatus.PAID) {
            throw new RuntimeException("Can only refund paid payments");
        }

        payment.setPaymentStatus(PaymentEntity.PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        PaymentEntity refundedPayment = paymentRepository.save(payment);

        // Update associated order status
        OrderEntity order = payment.getOrder();
        order.setStatus(OrderEntity.OrderStatus.REFUND);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return paymentMapper.toResponseDTO(refundedPayment);
    }

    /**
     * Calculate total payments for date range
     */
    public Double calculateTotalPayments(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.calculateTotalPaymentsForPeriod(startDate, endDate);
    }

    /**
     * Calculate total payments by method for date range
     */
    public Double calculateTotalPaymentsByMethod(String method, LocalDateTime startDate, LocalDateTime endDate) {
        PaymentEntity.PaymentMethod paymentMethod = PaymentEntity.PaymentMethod.valueOf(method);
        return paymentRepository.calculateTotalPaymentsByMethodForPeriod(paymentMethod, startDate, endDate);
    }
}