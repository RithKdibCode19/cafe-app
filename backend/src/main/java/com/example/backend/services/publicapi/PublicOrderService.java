package com.example.backend.services.publicapi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.publicapi.PublicOrderDTO;
import com.example.backend.model.AddOnEntity;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.OrderItemEntity;
import com.example.backend.model.VariantEntity;
import com.example.backend.repository.AddOnRepository;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.VariantRepository;

/**
 * Service for handling guest (no-auth) orders placed via QR code web menu.
 */
@Service
public class PublicOrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final AddOnRepository addOnRepository;

    public PublicOrderService(
            OrderRepository orderRepository,
            BranchRepository branchRepository,
            MenuItemRepository menuItemRepository,
            VariantRepository variantRepository,
            AddOnRepository addOnRepository) {
        this.orderRepository = orderRepository;
        this.branchRepository = branchRepository;
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
        this.addOnRepository = addOnRepository;
    }

    /**
     * Place a guest order from the QR web menu.
     */
    @Transactional
    public PublicOrderDTO.OrderResponse placeOrder(PublicOrderDTO.PlaceOrderRequest request) {
        // Validate branch by code
        BranchEntity branch = branchRepository.findByCode(request.getBranchCode())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        if (branch.getDeletedAt() != null) {
            throw new RuntimeException("Branch not found");
        }

        // Validate order type
        OrderEntity.OrderType orderType;
        try {
            orderType = OrderEntity.OrderType.valueOf(request.getOrderType());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order type. Use DINE_IN or TAKEAWAY");
        }

        // Generate order number
        String orderNo = generateOrderNo(branch.getCode());

        // Create the order
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        order.setBranch(branch);
        order.setCashierUser(null); // Guest orders have no cashier
        order.setCustomer(null); // Guest orders have no customer account
        order.setOrderType(orderType);
        order.setStatus(OrderEntity.OrderStatus.PENDING);
        order.setNote(request.getNote());
        order.setOrderSource("QR_WEB");
        order.setTableNo(request.getTableNo());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setPointsRedeemed(0);

        // Process items
        double subTotal = 0.0;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (PublicOrderDTO.OrderItemRequest itemReq : request.getItems()) {
            MenuItemEntity menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemReq.getMenuItemId()));

            if (menuItem.getIsAvailable() == null || !menuItem.getIsAvailable()) {
                throw new RuntimeException("Menu item is not available: " + menuItem.getName());
            }

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQty(itemReq.getQty());

            // Handle variant
            double unitPrice = menuItem.getBasePrice();
            if (itemReq.getVariantId() != null) {
                VariantEntity variant = variantRepository.findById(itemReq.getVariantId())
                        .orElseThrow(() -> new RuntimeException("Variant not found"));
                orderItem.setVariant(variant);
                unitPrice = variant.getPrice();
            }

            // Handle add-ons
            double addOnTotal = 0.0;
            if (itemReq.getAddonIds() != null && !itemReq.getAddonIds().isEmpty()) {
                List<AddOnEntity> addOns = addOnRepository.findAllById(itemReq.getAddonIds());
                orderItem.setAddOnItems(addOns);
                addOnTotal = addOns.stream().mapToDouble(AddOnEntity::getPrice).sum();
                orderItem.setAddons(addOns.stream().map(AddOnEntity::getName).collect(Collectors.joining(", ")));
            }

            orderItem.setUnitPrice(unitPrice + addOnTotal);
            orderItem.setDiscountAmount(0.0);
            orderItems.add(orderItem);

            subTotal += (unitPrice + addOnTotal) * itemReq.getQty();
        }

        order.setItems(orderItems);
        order.setSubTotal(subTotal);
        order.setDiscountAmount(0.0);
        order.setTaxAmount(0.0);
        order.setTotalAmount(subTotal);

        OrderEntity saved = orderRepository.save(order);

        return toOrderResponse(saved);
    }

    /**
     * Get order status by order number (for tracking page).
     */
    public PublicOrderDTO.OrderStatusResponse getOrderStatus(String orderNo) {
        OrderEntity order = orderRepository.findByOrderNoAndDeletedAtIsNull(orderNo);
        if (order == null) {
            return null;
        }

        return PublicOrderDTO.OrderStatusResponse.builder()
                .orderNo(order.getOrderNo())
                .status(order.getStatus().name())
                .tableNo(order.getTableNo())
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getItems() != null ? order.getItems().size() : 0)
                .createdAt(order.getCreatedAt() != null
                        ? order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .updatedAt(order.getUpdatedAt() != null
                        ? order.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

    /**
     * Get full order detail by order number (for order confirmation page).
     */
    public PublicOrderDTO.OrderResponse getOrderByOrderNo(String orderNo) {
        OrderEntity order = orderRepository.findByOrderNoAndDeletedAtIsNull(orderNo);
        if (order == null) {
            return null;
        }
        return toOrderResponse(order);
    }

    // ========== Helpers ==========

    private String generateOrderNo(String branchCode) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count() + 1;
        return "QR-" + branchCode + "-" + dateStr + "-" + String.format("%04d", count % 10000);
    }

    private PublicOrderDTO.OrderResponse toOrderResponse(OrderEntity order) {
        List<PublicOrderDTO.OrderItemResponse> items = List.of();
        if (order.getItems() != null) {
            items = order.getItems().stream().map(item -> {
                List<String> addonNames = List.of();
                if (item.getAddOnItems() != null) {
                    addonNames = item.getAddOnItems().stream()
                            .map(AddOnEntity::getName)
                            .collect(Collectors.toList());
                }

                return PublicOrderDTO.OrderItemResponse.builder()
                        .orderItemId(item.getOrderItemId())
                        .menuItemName(item.getMenuItem() != null ? item.getMenuItem().getName() : "Unknown")
                        .menuItemImage(item.getMenuItem() != null ? item.getMenuItem().getImageUrl() : null)
                        .variantSize(item.getVariant() != null ? item.getVariant().getSize().name() : null)
                        .qty(item.getQty())
                        .unitPrice(item.getUnitPrice())
                        .addonNames(addonNames)
                        .build();
            }).collect(Collectors.toList());
        }

        return PublicOrderDTO.OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType().name())
                .status(order.getStatus().name())
                .tableNo(order.getTableNo())
                .note(order.getNote())
                .subTotal(order.getSubTotal())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .customerName(order.getCustomerName())
                .items(items)
                .createdAt(order.getCreatedAt() != null
                        ? order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }
}
