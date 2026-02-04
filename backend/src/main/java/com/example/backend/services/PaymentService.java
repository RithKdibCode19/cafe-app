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

        // 3. Validation: Amount Sufficiency
        Double orderTotal = order.getTotalAmount();
        Double paidAmount = request.getPaidAmount();

        if (paidAmount < orderTotal) {
            throw new RuntimeException(
                    "Insufficient payment amount. Required: " + orderTotal + ", Provided: " + paidAmount);
        }

        // 4. Calculate Change
        Double changeAmount = paidAmount - orderTotal;
        // Make sure we deal with floating point precision if needed, but for now simple
        // subtraction is okay for Double.
        // Ideally use BigDecimal for money, but Double is what we have.
        if (changeAmount < 0)
            changeAmount = 0.0;

        // 5. Map Request DTO → Entity
        PaymentEntity payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setMethod(PaymentEntity.PaymentMethod.valueOf(request.getMethod()));

        // Force status to PAID if sufficient (ignoring DTO status if logic dictates)
        // Or strictly follow DTO but validate. Let's trust logic over DTO for status if
        // successful.
        // Actually, if we are processing a payment, it's usually meaningful.
        // Let's use the DTO status but ensure it makes sense.
        // For simplicity, if we are here and amount is sufficient, we treat it as PAID.
        payment.setPaymentStatus(PaymentEntity.PaymentStatus.PAID);

        payment.setPaidAmount(paidAmount);
        payment.setChangeAmount(changeAmount);
        payment.setPaidAt(request.getPaidAt() != null ? request.getPaidAt() : LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        // 6. Save payment
        PaymentEntity savedPayment = paymentRepository.save(payment);

        // 7. Update order status
        order.setStatus(OrderEntity.OrderStatus.PAID);
        // We could also link the payment to the order if the relationship was
        // bidirectional and we wanted to cache it,
        // but OrderEntity doesn't seem to hold a direct reference to a specific payment
        // (OneToMany usually).
        // Check OrderEntity... it doesn't have a Payment field, so just status update.
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

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
        // Update associated order status
        OrderEntity order = payment.getOrder();
        order.setStatus(OrderEntity.OrderStatus.REFUND);

        // Log reason to order note
        if (refundReason != null && !refundReason.isEmpty()) {
            String existingNote = order.getNote() != null ? order.getNote() : "";
            order.setNote(existingNote + " | [REFUND] Reason: " + refundReason);
        }

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