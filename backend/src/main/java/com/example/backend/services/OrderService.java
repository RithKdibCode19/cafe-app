package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.OrderItemRequestDTO;
import com.example.backend.dto.OrderRequestDTO;
import com.example.backend.dto.OrderResponseDTO;
import com.example.backend.mapper.OrderMapper;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.CustomerEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.OrderItemEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.model.VariantEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final OrderMapper orderMapper;

    /**
     * Create a new order with all order items
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // 1. Generate order number if not provided
        if (request.getOrderNo() == null || request.getOrderNo().trim().isEmpty()) {
            request.setOrderNo(generateOrderNumber());
        }

        // 2. Validate and fetch required entities
        BranchEntity branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + request.getBranchId()));

        UserEntity cashierUser = userRepository.findById(request.getCashierUserId())
                .orElseThrow(
                        () -> new RuntimeException("Cashier user not found with ID: " + request.getCashierUserId()));

        CustomerEntity customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));
        }

        // 3. Map Request DTO → Entity
        OrderEntity order = orderMapper.toEntity(request);
        order.setBranch(branch);
        order.setCashierUser(cashierUser);
        order.setCustomer(customer);
        order.setOrderType(OrderEntity.OrderType.valueOf(request.getOrderType()));
        order.setStatus(OrderEntity.OrderStatus.valueOf(request.getStatus()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 4. Process order items
        List<OrderItemEntity> orderItems = request.getItems().stream()
                .map(itemRequest -> createOrderItem(order, itemRequest))
                .collect(Collectors.toList());
        order.setItems(orderItems);

        // 5. Save order (cascade will save items)
        OrderEntity savedOrder = orderRepository.save(order);

        // 6. Map Entity → Response DTO
        return orderMapper.toResponseDTO(savedOrder);
    }

    /**
     * Get all orders
     */
    public List<OrderResponseDTO> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream()
                .filter(order -> order.getDeletedAt() == null) // Only active orders
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     */
    public OrderResponseDTO getOrderById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (order.getDeletedAt() != null) {
            throw new RuntimeException("Order has been deleted");
        }

        return orderMapper.toResponseDTO(order);
    }

    /**
     * Get orders by branch
     */
    public List<OrderResponseDTO> getOrdersByBranch(Long branchId) {
        List<OrderEntity> orders = orderRepository.findByBranchBranchIdAndDeletedAtIsNull(branchId);
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        OrderEntity.OrderStatus orderStatus = OrderEntity.OrderStatus.valueOf(status);
        List<OrderEntity> orders = orderRepository.findByStatusAndDeletedAtIsNull(orderStatus);
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by cashier
     */
    public List<OrderResponseDTO> getOrdersByCashier(Long cashierUserId) {
        List<OrderEntity> orders = orderRepository.findByCashierUserUserIdAndDeletedAtIsNull(cashierUserId);
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by date range
     */
    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderEntity> orders = orderRepository.findByCreatedAtBetweenAndDeletedAtIsNull(startDate, endDate);
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (order.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted order");
        }

        order.setStatus(OrderEntity.OrderStatus.valueOf(status));
        order.setUpdatedAt(LocalDateTime.now());

        OrderEntity updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    /**
     * Update entire order
     */
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO request) {
        OrderEntity existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (existingOrder.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted order");
        }

        // Validate status transition (business rule: can't change PAID orders)
        if (existingOrder.getStatus() == OrderEntity.OrderStatus.PAID) {
            throw new RuntimeException("Cannot modify paid orders");
        }

        // Update basic fields
        orderMapper.updateEntityFromDTO(request, existingOrder);
        existingOrder.setOrderType(OrderEntity.OrderType.valueOf(request.getOrderType()));
        existingOrder.setStatus(OrderEntity.OrderStatus.valueOf(request.getStatus()));
        existingOrder.setUpdatedAt(LocalDateTime.now());

        // Update relationships if changed
        if (!existingOrder.getBranch().getBranchId().equals(request.getBranchId())) {
            BranchEntity branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            existingOrder.setBranch(branch);
        }

        if (!existingOrder.getCashierUser().getUserId().equals(request.getCashierUserId())) {
            UserEntity cashierUser = userRepository.findById(request.getCashierUserId())
                    .orElseThrow(() -> new RuntimeException("Cashier user not found"));
            existingOrder.setCashierUser(cashierUser);
        }

        // Update customer if changed
        if (request.getCustomerId() != null) {
            if (existingOrder.getCustomer() == null ||
                    !existingOrder.getCustomer().getCustomerId().equals(request.getCustomerId())) {
                CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
                existingOrder.setCustomer(customer);
            }
        } else {
            existingOrder.setCustomer(null);
        }

        OrderEntity updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    /**
     * Soft delete order
     */
    @Transactional
    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (order.getStatus() == OrderEntity.OrderStatus.PAID) {
            throw new RuntimeException("Cannot delete paid orders");
        }

        order.setDeletedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    /**
     * Search orders by order number
     */
    public List<OrderResponseDTO> searchOrdersByOrderNo(String orderNo) {
        List<OrderEntity> orders = orderRepository.findByOrderNoContainingIgnoreCaseAndDeletedAtIsNull(orderNo);
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get today's orders for a branch
     */
    public List<OrderResponseDTO> getTodayOrdersForBranch(Long branchId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<OrderEntity> orders = orderRepository.findByBranchBranchIdAndCreatedAtBetweenAndDeletedAtIsNull(
                branchId, startOfDay, endOfDay);

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private OrderItemEntity createOrderItem(OrderEntity order, OrderItemRequestDTO itemRequest) {
        MenuItemEntity menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + itemRequest.getMenuItemId()));

        VariantEntity variant = null;
        if (itemRequest.getVariantId() != null) {
            variant = variantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(
                            () -> new RuntimeException("Variant not found with ID: " + itemRequest.getVariantId()));
        }

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setVariant(variant);
        orderItem.setQty(itemRequest.getQuantity());
        orderItem.setUnitPrice(itemRequest.getUnitPrice());
        // Map note from request to addons field in entity (for special instructions)
        orderItem.setAddons(itemRequest.getNote());
        orderItem.setCreatedAt(LocalDateTime.now());
        orderItem.setUpdatedAt(LocalDateTime.now());

        return orderItem;
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
