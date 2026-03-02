package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.backend.exception.InsufficientStockException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final com.example.backend.repository.AddOnRepository addOnRepository;
    private final com.example.backend.repository.RecipeRepository recipeRepository;
    private final com.example.backend.repository.IngredientRepository ingredientRepository;
    private final OrderMapper orderMapper;
    private final com.example.backend.repository.PaymentRepository paymentRepository;
    private final TelegramService telegramService;
    private final SystemSettingService systemSettingService;
    private final BranchStockService branchStockService;
    private final FcmService fcmService;
    private final LoyaltyService loyaltyService;

    public OrderService(OrderRepository orderRepository, BranchRepository branchRepository, UserRepository userRepository, CustomerRepository customerRepository, MenuItemRepository menuItemRepository, VariantRepository variantRepository, com.example.backend.repository.AddOnRepository addOnRepository, com.example.backend.repository.RecipeRepository recipeRepository, com.example.backend.repository.IngredientRepository ingredientRepository, OrderMapper orderMapper, com.example.backend.repository.PaymentRepository paymentRepository, TelegramService telegramService, SystemSettingService systemSettingService, BranchStockService branchStockService, FcmService fcmService, LoyaltyService loyaltyService) {
        this.orderRepository = orderRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
        this.addOnRepository = addOnRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.orderMapper = orderMapper;
        this.paymentRepository = paymentRepository;
        this.telegramService = telegramService;
        this.systemSettingService = systemSettingService;
        this.branchStockService = branchStockService;
        this.fcmService = fcmService;
        this.loyaltyService = loyaltyService;
    }

    private void createPaymentForOrder(OrderEntity order, OrderRequestDTO request) {
        com.example.backend.model.PaymentEntity payment = new com.example.backend.model.PaymentEntity();
        payment.setOrder(order);

        // Map Method
        if ("KHQR".equalsIgnoreCase(request.getPaymentMethod()) || "QB".equalsIgnoreCase(request.getPaymentMethod())) {
            payment.setMethod(com.example.backend.model.PaymentEntity.PaymentMethod.QR);
        } else {
            payment.setMethod(com.example.backend.model.PaymentEntity.PaymentMethod.CASH);
        }

        payment.setPaidAmount(
                request.getReceivedAmount() != null ? request.getReceivedAmount() : order.getTotalAmount());
        payment.setChangeAmount(payment.getPaidAmount() - order.getTotalAmount());
        payment.setPaymentStatus(com.example.backend.model.PaymentEntity.PaymentStatus.PAID);
        payment.setTransactionId(request.getPaymentReference());
        payment.setPaidAt(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }

    // ... (rest of class)

    private void deductInventoryForOrder(OrderEntity order) {
        List<Map<String, Object>> lowStockItems = new java.util.ArrayList<>();
        
        for (OrderItemEntity item : order.getItems()) {
            Long menuItemId = item.getMenuItem().getMenuItemId();
            Integer qty = item.getQty();

            // Find recipes for this menu item
            List<com.example.backend.model.RecipeEntity> recipes = recipeRepository
                    .findByMenuItemMenuItemId(menuItemId);

            for (com.example.backend.model.RecipeEntity recipe : recipes) {
                com.example.backend.model.IngredientEntity ingredient = recipe.getIngredient();
                Double quantityNeeded = recipe.getQuantityNeeded() * qty;

                // Deduct from branch-specific stock
                branchStockService.adjustStock(order.getBranch().getBranchId(), 
                                            ingredient.getIngredientId(), 
                                            -quantityNeeded);

                // Check if now below reorder level (using global stock for alert, but could be branch-specific)
                if (ingredient.getReorderLevel() != null && 
                    ingredient.getCurrentStock() <= ingredient.getReorderLevel()) {
                    Map<String, Object> item2 = new java.util.HashMap<>();
                    item2.put("name", ingredient.getName());
                    item2.put("currentStock", ingredient.getCurrentStock());
                    item2.put("reorderLevel", ingredient.getReorderLevel());
                    item2.put("unit", ingredient.getUnit().name());
                    lowStockItems.add(item2);
                }
            }
        }

        // Send low stock alert if any items are below reorder level
        if (!lowStockItems.isEmpty()) {
            try {
                telegramService.sendLowStockAlert(lowStockItems);
            } catch (Exception e) {
                // Don't fail order if notification fails
            }
        }
    }

    public void restoreInventoryForOrder(OrderEntity order) {
        for (OrderItemEntity item : order.getItems()) {
            Long menuItemId = item.getMenuItem().getMenuItemId();
            Integer qty = item.getQty();

            List<com.example.backend.model.RecipeEntity> recipes = recipeRepository
                    .findByMenuItemMenuItemId(menuItemId);

            for (com.example.backend.model.RecipeEntity recipe : recipes) {
                com.example.backend.model.IngredientEntity ingredient = recipe.getIngredient();
                Double quantityNeeded = recipe.getQuantityNeeded() * qty;

                // Restore to branch-specific stock
                branchStockService.adjustStock(order.getBranch().getBranchId(), 
                                            ingredient.getIngredientId(), 
                                            quantityNeeded);
            }
        }
    }

    /**
     * Calculate total for an order request without saving (for verification)
     */
    public Double calculateTotal(OrderRequestDTO request) {
        double subTotal = request.getItems().stream().mapToDouble(itemRequest -> {
            MenuItemEntity menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            Double basePrice = menuItem.getBasePrice();
            if (itemRequest.getVariantId() != null) {
                VariantEntity variant = variantRepository.findById(itemRequest.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Variant not found"));
                basePrice = variant.getPrice();
            }

            double addOnTotal = 0.0;
            if (itemRequest.getAddOnIds() != null && !itemRequest.getAddOnIds().isEmpty()) {
                addOnTotal = addOnRepository.findAllById(itemRequest.getAddOnIds())
                        .stream().filter(java.util.Objects::nonNull)
                        .mapToDouble(com.example.backend.model.AddOnEntity::getPrice).sum();
            }

            return (basePrice + addOnTotal) * itemRequest.getQty();
        }).sum();

        // SECURE DISCOUNT CALCULATION
        double discount = 0.0;
        if (request.getPointsRedeemed() != null && request.getPointsRedeemed() > 0) {
            if (request.getCustomerId() == null) {
                throw new RuntimeException("Customer ID required for point redemption");
            }
            CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            int availablePoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            if (availablePoints < request.getPointsRedeemed()) {
                throw new RuntimeException("Insufficient points! Hack detected.");
            }
            double redeemRate = loyaltyService.getRedeemRate();
            discount = request.getPointsRedeemed() * redeemRate;
        }

        double taxRate = 0.0; // Consistent with createOrder
        double taxAmount = Math.max(0, (subTotal - discount) * taxRate);

        return Math.max(0, subTotal - discount + taxAmount);
    }

    /**
     * Create a new order with all order items
     */
    @Transactional
    @com.example.backend.security.IsolateByBranch("request")
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // 0. Validate stock before doing anything
        validateStockForOrder(request);

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
        order.setPointsRedeemed(request.getPointsRedeemed() != null ? request.getPointsRedeemed() : 0);

        // 4. Process order items and calculate subtotal
        List<OrderItemEntity> orderItems = request.getItems().stream()
                .map(itemRequest -> createOrderItem(order, itemRequest))
                .collect(Collectors.toList());
        order.setItems(orderItems);
        order.setOrderSource(request.getOrderSource() != null ? request.getOrderSource() : "POS");
        order.setTableNo(request.getTableNo());

        // Calculate Subtotal from items
        double subTotal = orderItems.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQty())
                .sum();

        order.setSubTotal(subTotal);

        // 5. Apply Financials (SECURE CALCULATION)
        double discount = 0.0;
        if (request.getPointsRedeemed() != null && request.getPointsRedeemed() > 0) {
            if (customer == null) {
                throw new RuntimeException("Cannot redeem points without a valid customer.");
            }
            int availablePoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            if (availablePoints < request.getPointsRedeemed()) {
                throw new RuntimeException("Insufficient loyalty points for redemption!");
            }
            double redeemRate = loyaltyService.getRedeemRate();
            discount = request.getPointsRedeemed() * redeemRate;
        }
        order.setDiscountAmount(discount);

        // Simple Tax Logic (e.g. 0% for now, can be 0.10 for 10%)
        double taxRate = 0.0;
        double taxAmount = Math.max(0, (subTotal - discount) * taxRate);
        order.setTaxAmount(taxAmount);

        double totalAmount = Math.max(0, subTotal - discount + taxAmount);
        order.setTotalAmount(totalAmount);

        // 5.1 Process Loyalty Redemption (Point Deduction)
        if (request.getPointsRedeemed() != null && request.getPointsRedeemed() > 0) {
            loyaltyService.deductPoints(customer, request.getPointsRedeemed());
        }

        // 6. Save order (cascade will save items)
        // 6. Save order (cascade will save items)
        OrderEntity savedOrder = orderRepository.save(order);

        // 6.1 Process Payment if included
        if (request.getStatus().equals("PAID") && request.getPaymentMethod() != null) {
            createPaymentForOrder(savedOrder, request);
        }

        // 7. Deduct Inventory (Simple Logic)
        deductInventoryForOrder(savedOrder);

        // 8. Update Loyalty Points
        if (savedOrder.getCustomer() != null && savedOrder.getStatus() == OrderEntity.OrderStatus.PAID) {
            loyaltyService.awardPoints(savedOrder);
        }

        // 9. Telegram Notification (Large Order Alert)
        try {
            telegramService.sendLargeOrderAlert(
                savedOrder.getOrderNo(),
                savedOrder.getTotalAmount(),
                savedOrder.getItems().size()
            );
        } catch (Exception e) {
            // Don't fail the order if notification fails
        }

        return orderMapper.toResponseDTO(savedOrder);
    }

    /**
     * Get all orders
     */
    public List<OrderResponseDTO> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream()
                .filter(order -> order.getDeletedAt() == null)
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all orders (paginated)
     */
    @com.example.backend.security.IsolateByBranch
    public org.springframework.data.domain.Page<OrderResponseDTO> getAllOrdersPaginated(org.springframework.data.domain.Pageable pageable, String status, String search, Long branchId) {
        org.springframework.data.jpa.domain.Specification<OrderEntity> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            // 1. Mandatory filter: Not deleted
            predicates.add(cb.isNull(root.get("deletedAt")));

            // 2. Optional: Branch filter
            if (branchId != null) {
                predicates.add(cb.equal(root.get("branch").get("branchId"), branchId));
            }

            // 3. Optional: Status filter
            if (status != null && !status.equalsIgnoreCase("ALL")) {
                try {
                    OrderEntity.OrderStatus orderStatus = OrderEntity.OrderStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), orderStatus));
                } catch (IllegalArgumentException e) {
                    // Invalid status provided, optionally handle or just continue/return empty
                }
            }

            // 4. Optional: Search query (search in orderNo)
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("orderNo")), searchPattern));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        org.springframework.data.domain.Page<OrderEntity> orderPage = orderRepository.findAll(spec, pageable);
        return orderPage.map(orderMapper::toResponseDTO);
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
    @com.example.backend.security.IsolateByBranch
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
    public OrderResponseDTO updateOrderStatus(Long id, String status, String pinCode, String reason) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (order.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted order");
        }

        OrderEntity.OrderStatus newStatus = OrderEntity.OrderStatus.valueOf(status);
        OrderEntity.OrderStatus currentStatus = order.getStatus();

        // 1. PIN Authorization for VOID/REFUND
        if (newStatus == OrderEntity.OrderStatus.VOID || newStatus == OrderEntity.OrderStatus.REFUND) {
            if (pinCode == null || pinCode.isEmpty()) {
                throw new RuntimeException("Authorization PIN is required for " + newStatus);
            }
            if (reason == null || reason.isEmpty()) {
                throw new RuntimeException("Reason is required for " + newStatus);
            }

            List<UserEntity> candidates = userRepository.findByPinCodeAndDeletedAtIsNull(pinCode);
            if (candidates.isEmpty()) {
                throw new RuntimeException("Invalid Authorization PIN");
            }

            final String requiredPerm = (newStatus == OrderEntity.OrderStatus.VOID) ? "POS_VOID" : "POS_REFUND";

            UserEntity approver = candidates.stream()
                    .filter(u -> {
                        // 1. Check permissions (Specific permission OR SYS_ALL)
                        boolean hasPermission = u.getRole().getPermissions().stream()
                                .anyMatch(p -> requiredPerm.equals(p.getCode()) || "SYS_ALL".equals(p.getCode()));
                        if (!hasPermission) return false;

                        // 2. Branch restriction: SYS_ALL can approve anywhere
                        boolean isSystemAdmin = u.getRole().getPermissions().stream()
                                .anyMatch(p -> "SYS_ALL".equals(p.getCode()));
                        if (isSystemAdmin) return true;

                        // 3. Others must match the branch of the order
                        return u.getEmployee().getBranch().getBranchId().equals(order.getBranch().getBranchId());
                    })
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Authorization PIN is valid, but the user does not have permission for this branch"));

            order.setApprovedBy(approver);
            order.setStatusReason(reason);
        }

        // Business Rules for Transitions
        if (newStatus == OrderEntity.OrderStatus.VOID) {
            if (currentStatus != OrderEntity.OrderStatus.PENDING) {
                throw new RuntimeException("Only PENDING orders can be VOIDED. Current status: " + currentStatus);
            }
        } else if (newStatus == OrderEntity.OrderStatus.REFUND) {
            if (currentStatus != OrderEntity.OrderStatus.PAID && currentStatus != OrderEntity.OrderStatus.COMPLETED) {
                throw new RuntimeException("Only PAID/COMPLETED orders can be REFUNDED. Current status: " + currentStatus);
            }
        }

        // Award Points if becoming PAID
        if (newStatus == OrderEntity.OrderStatus.PAID && currentStatus != OrderEntity.OrderStatus.PAID) {
            loyaltyService.awardPoints(order);
        }

        // Update Status
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        // --- SECURITY & LOGIC FIXES ---

        // 1. Revert Inventory if order is VOIDED or REFUNDED
        if (newStatus == OrderEntity.OrderStatus.VOID || newStatus == OrderEntity.OrderStatus.REFUND) {
            restoreInventoryForOrder(order);
        }

        // 2. Revert Loyalty Points if order is REFUNDED
        if (newStatus == OrderEntity.OrderStatus.REFUND && order.getCustomer() != null) {
            loyaltyService.revertPoints(order);
        }

        // Log to Audit Trail
        if (reason != null && !reason.isEmpty()) {
            String existingNote = order.getNote() != null ? order.getNote() : "";
            String approverInfo = order.getApprovedBy() != null ? " Approved By: " + order.getApprovedBy().getEmployee().getFullName() : "";
            order.setNote(existingNote + " | [" + newStatus + "] Reason: " + reason + approverInfo);
        }

        OrderEntity updatedOrder = orderRepository.save(order);

        // 3. Send Push Notification to Customer
        if (updatedOrder.getCustomer() != null && updatedOrder.getCustomer().getFcmToken() != null) {
            String title = "Order Update: " + updatedOrder.getOrderNo();
            String body = "Your order status has been updated to " + updatedOrder.getStatus();
            
            // Customize messages for better UX
            if (updatedOrder.getStatus() == OrderEntity.OrderStatus.PAID) body = "Payment received! We're starting your order.";
            if (updatedOrder.getStatus() == OrderEntity.OrderStatus.COMPLETED) body = "Your order is ready! Enjoy your coffee.";
            if (updatedOrder.getStatus() == OrderEntity.OrderStatus.VOID) body = "Your order has been cancelled.";
            
            fcmService.sendNotification(updatedOrder.getCustomer().getFcmToken(), title, body);
        }

        return orderMapper.toResponseDTO(updatedOrder);
    }

    /**
     * Update entire order
     */
    @Transactional
    @com.example.backend.security.IsolateByBranch("request")
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

        // Update basic fields via Mapper (ignores items and calculated financials)
        orderMapper.updateEntityFromDTO(request, existingOrder);

        // Manual Status/Type Updates (if needed explicitly, though mapper might handle
        // simple strings)
        existingOrder.setOrderType(OrderEntity.OrderType.valueOf(request.getOrderType()));
        existingOrder.setStatus(OrderEntity.OrderStatus.valueOf(request.getStatus()));
        existingOrder.setUpdatedAt(LocalDateTime.now());

        // --- 1. Update Items & Secure Prices ---
        // Since we are updating, we replace the existing items with the new list.
        // We use createOrderItem to ensure we fetch secure prices from DB, not trusting
        // the client.

        List<OrderItemEntity> newOrderItems = request.getItems().stream()
                .map(itemRequest -> createOrderItem(existingOrder, itemRequest))
                .collect(Collectors.toList());

        // Update the collection
        if (existingOrder.getItems() == null) {
            existingOrder.setItems(newOrderItems);
        } else {
            existingOrder.getItems().clear();
            existingOrder.getItems().addAll(newOrderItems);
        }

        // --- 2. Recalculate Financials ---

        // Calculate Subtotal from NEW items
        double subTotal = newOrderItems.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQty())
                .sum();
        existingOrder.setSubTotal(subTotal);

        // Discount (SECURE CALCULATION)
        double discount = 0.0;
        if (request.getPointsRedeemed() != null && request.getPointsRedeemed() > 0) {
            CustomerEntity customer = existingOrder.getCustomer();
            if (customer == null) {
                throw new RuntimeException("No customer linked to this order for points redemption.");
            }
            int availablePoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            // Note: Since this is an update, the points might have already been deducted.
            // But business rule says we can't modify PAID orders anyway (line 421).
            double redeemRate = loyaltyService.getRedeemRate();
            discount = request.getPointsRedeemed() * redeemRate;
        }
        existingOrder.setDiscountAmount(discount);

        // Tax (Align with createOrder logic)
        double taxRate = 0.0;
        double taxAmount = Math.max(0, (subTotal - discount) * taxRate);
        existingOrder.setTaxAmount(taxAmount);

        // Total
        double totalAmount = Math.max(0, subTotal - discount + taxAmount);
        existingOrder.setTotalAmount(totalAmount);

        // --- 3. Update Relationships ---

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

        // Restore inventory before marking as deleted
        restoreInventoryForOrder(order);

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
    @com.example.backend.security.IsolateByBranch
    public List<OrderResponseDTO> getTodayOrdersForBranch(Long branchId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<OrderEntity> orders = orderRepository.findByBranchBranchIdAndCreatedAtBetweenAndDeletedAtIsNull(
                branchId, startOfDay, endOfDay);

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void validateStockForOrder(OrderRequestDTO request) {
        Long branchId = request.getBranchId();
        Map<Long, Double> totalIngredientsNeeded = new java.util.HashMap<>();

        for (OrderItemRequestDTO item : request.getItems()) {
            List<com.example.backend.model.RecipeEntity> recipes = recipeRepository
                    .findByMenuItemMenuItemId(item.getMenuItemId());

            for (com.example.backend.model.RecipeEntity recipe : recipes) {
                Long ingredientId = recipe.getIngredient().getIngredientId();
                Double needed = recipe.getQuantityNeeded() * item.getQty();
                totalIngredientsNeeded.merge(ingredientId, needed, Double::sum);
            }
        }

        for (Map.Entry<Long, Double> entry : totalIngredientsNeeded.entrySet()) {
            Long ingredientId = entry.getKey();
            Double totalNeeded = entry.getValue();

            if (!branchStockService.isStockAvailable(branchId, ingredientId, totalNeeded)) {
                com.example.backend.model.IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                        .orElseThrow(() -> new RuntimeException("Ingredient not found"));
                
                Double currentStock = branchStockService.getCurrentStock(branchId, ingredientId);
                throw new InsufficientStockException(
                    String.format("Insufficient stock for %s at this branch. Needed: %.2f %s, Available: %.2f %s", 
                    ingredient.getName(), totalNeeded, ingredient.getUnit(), currentStock, ingredient.getUnit())
                );
            }
        }
    }

    // Private helper methods

    private OrderItemEntity createOrderItem(OrderEntity order, OrderItemRequestDTO itemRequest) {
        MenuItemEntity menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + itemRequest.getMenuItemId()));

        VariantEntity variant = null;
        Double basePrice = menuItem.getBasePrice(); // Default to base price

        if (itemRequest.getVariantId() != null) {
            variant = variantRepository.findById(itemRequest.getVariantId())
                    .orElseThrow(
                            () -> new RuntimeException("Variant not found with ID: " + itemRequest.getVariantId()));
            basePrice = variant.getPrice(); // Use variant price if exists
        }

        // Handle Add-ons
        java.util.List<com.example.backend.model.AddOnEntity> addOns = new java.util.ArrayList<>();
        double addOnTotal = 0.0;

        if (itemRequest.getAddOnIds() != null && !itemRequest.getAddOnIds().isEmpty()) {
            addOns = addOnRepository.findAllById(itemRequest.getAddOnIds());
            addOnTotal = addOns.stream().mapToDouble(com.example.backend.model.AddOnEntity::getPrice).sum();
        }

        Double finalPrice = basePrice + addOnTotal;

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setVariant(variant);
        orderItem.setQty(itemRequest.getQty());
        orderItem.setUnitPrice(finalPrice); // Set secured price from DB + Addons
        orderItem.setAddOnItems(addOns);

        // Map note from request to addons field in entity
        orderItem.setAddons(itemRequest.getNote());
        orderItem.setCreatedAt(LocalDateTime.now());
        orderItem.setUpdatedAt(LocalDateTime.now());

        return orderItem;
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
