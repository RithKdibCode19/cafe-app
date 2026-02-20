package com.example.backend.services.mobile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.mobile.MobileOrderDTO;
import com.example.backend.model.AddOnEntity;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.CustomerEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.OrderItemEntity;
import com.example.backend.model.VariantEntity;
import com.example.backend.repository.AddOnRepository;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.VariantRepository;
import com.example.backend.services.FcmService;
import com.example.backend.services.SystemSettingService;

@Service
public class MobileOrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final AddOnRepository addOnRepository;
    private final SystemSettingService systemSettingService;
    private final FcmService fcmService;

    public MobileOrderService(
            OrderRepository orderRepository,
            BranchRepository branchRepository,
            CustomerRepository customerRepository,
            MenuItemRepository menuItemRepository,
            VariantRepository variantRepository,
            AddOnRepository addOnRepository,
            SystemSettingService systemSettingService,
            FcmService fcmService) {
        this.orderRepository = orderRepository;
        this.branchRepository = branchRepository;
        this.customerRepository = customerRepository;
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
        this.addOnRepository = addOnRepository;
        this.systemSettingService = systemSettingService;
        this.fcmService = fcmService;
    }

    /**
     * Place a new order from the mobile app.
     */
    @Transactional
    public MobileOrderDTO.OrderResponse placeOrder(Long customerId, MobileOrderDTO.PlaceOrderRequest request) {
        // Validate branch
        BranchEntity branch = branchRepository.findActiveBranchById(request.getBranchId());
        if (branch == null) {
            throw new RuntimeException("Branch not found");
        }

        // Validate customer
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate order type
        OrderEntity.OrderType orderType;
        try {
            orderType = OrderEntity.OrderType.valueOf(request.getOrderType());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order type. Use TAKEAWAY or DELIVERY");
        }

        // For DELIVERY, require address
        if (orderType == OrderEntity.OrderType.DELIVERY) {
            if (request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty()) {
                throw new RuntimeException("Delivery address is required for DELIVERY orders");
            }
        }

        // Generate order number
        String orderNo = generateOrderNo(branch.getCode());

        // Create the order
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        order.setBranch(branch);
        order.setCashierUser(null); // Mobile orders have no cashier
        order.setCustomer(customer);
        order.setOrderType(orderType);
        order.setStatus(OrderEntity.OrderStatus.PENDING);
        order.setNote(request.getNote());
        order.setOrderSource("MOBILE");
        order.setPointsRedeemed(request.getPointsRedeemed() != null ? request.getPointsRedeemed() : 0);

        // Set delivery fields
        if (orderType == OrderEntity.OrderType.DELIVERY) {
            order.setDeliveryAddress(request.getDeliveryAddress());
            order.setDeliveryPhone(request.getDeliveryPhone() != null ? request.getDeliveryPhone() : customer.getPhone());
            order.setDeliveryNote(request.getDeliveryNote());
            order.setDeliveryFee(1.0); // Default $1 delivery fee — can be configured
        }

        // Process items
        double subTotal = 0.0;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (MobileOrderDTO.OrderItemRequest itemReq : request.getItems()) {
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
                // Store add-on names as text
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
        order.setTaxAmount(0.0); // Can be configured via SystemSettings
        order.setTotalAmount(subTotal + (order.getDeliveryFee() != null ? order.getDeliveryFee() : 0.0));

        // Handle loyalty points redemption
        if (request.getPointsRedeemed() != null && request.getPointsRedeemed() > 0) {
            if (customer.getLoyaltyPoints() < request.getPointsRedeemed()) {
                throw new RuntimeException("Insufficient loyalty points");
            }
            // Fetch redeem rate from settings (default to 0.01 if not found, i.e., 100 points = $1)
            double redeemRate = 0.01;
            String rateStr = systemSettingService.getValue("LOYALTY_REDEEM_RATE");
            if (rateStr != null) {
                try {
                    redeemRate = Double.parseDouble(rateStr);
                } catch (NumberFormatException e) {
                    // fall back to default
                }
            }
            
            double discount = request.getPointsRedeemed() * redeemRate;
            order.setDiscountAmount(discount);
            order.setTotalAmount(Math.max(0.0, order.getTotalAmount() - discount));

            // Deduct points
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() - request.getPointsRedeemed());
            customerRepository.save(customer);
        }

        OrderEntity saved = orderRepository.save(order);

        return toOrderResponse(saved);
    }

    /**
     * Get customer's order history.
     */
    public List<MobileOrderDTO.OrderSummary> getOrderHistory(Long customerId) {
        List<OrderEntity> orders = orderRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId);
        return orders.stream()
                .map(this::toOrderSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get single order details (customer must own the order).
     */
    public MobileOrderDTO.OrderResponse getOrderDetail(Long customerId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getCustomer() == null || !order.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("Order not found");
        }

        return toOrderResponse(order);
    }

    /**
     * Cancel a PENDING order.
     */
    @Transactional
    public MobileOrderDTO.OrderResponse cancelOrder(Long customerId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getCustomer() == null || !order.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("Order not found");
        }

        if (order.getStatus() != OrderEntity.OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be cancelled");
        }

        order.setStatus(OrderEntity.OrderStatus.VOID);
        order.setStatusReason("Cancelled by customer via mobile app");

        // Refund loyalty points if any were used
        if (order.getPointsRedeemed() != null && order.getPointsRedeemed() > 0) {
            CustomerEntity customer = order.getCustomer();
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + order.getPointsRedeemed());
            customerRepository.save(customer);
        }

        OrderEntity saved = orderRepository.save(order);

        // Send Push Notification
        if (saved.getCustomer().getFcmToken() != null) {
            fcmService.sendNotification(
                saved.getCustomer().getFcmToken(),
                "Order Cancelled",
                "Your order " + saved.getOrderNo() + " has been successfully cancelled."
            );
        }

        return toOrderResponse(saved);
    }

    // ========== Helpers ==========

    private String generateOrderNo(String branchCode) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count() + 1;
        return "MOB-" + branchCode + "-" + dateStr + "-" + String.format("%04d", count % 10000);
    }

    private MobileOrderDTO.OrderResponse toOrderResponse(OrderEntity order) {
        List<MobileOrderDTO.OrderItemResponse> items = List.of();
        if (order.getItems() != null) {
            items = order.getItems().stream().map(item -> {
                List<String> addonNames = List.of();
                if (item.getAddOnItems() != null) {
                    addonNames = item.getAddOnItems().stream()
                            .map(AddOnEntity::getName)
                            .collect(Collectors.toList());
                }

                return MobileOrderDTO.OrderItemResponse.builder()
                        .orderItemId(item.getOrderItemId())
                        .menuItemName(item.getMenuItem() != null ? item.getMenuItem().getName() : "Unknown")
                        .variantSize(item.getVariant() != null ? item.getVariant().getSize().name() : null)
                        .qty(item.getQty())
                        .unitPrice(item.getUnitPrice())
                        .addonNames(addonNames)
                        .build();
            }).collect(Collectors.toList());
        }

        return MobileOrderDTO.OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType().name())
                .status(order.getStatus().name())
                .note(order.getNote())
                .subTotal(order.getSubTotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .deliveryFee(order.getDeliveryFee())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPhone(order.getDeliveryPhone())
                .pointsRedeemed(order.getPointsRedeemed())
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .items(items)
                .createdAt(order.getCreatedAt() != null
                        ? order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

    private MobileOrderDTO.OrderSummary toOrderSummary(OrderEntity order) {
        return MobileOrderDTO.OrderSummary.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .orderType(order.getOrderType().name())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .branchName(order.getBranch() != null ? order.getBranch().getName() : null)
                .itemCount(order.getItems() != null ? order.getItems().size() : 0)
                .createdAt(order.getCreatedAt() != null
                        ? order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }
}
