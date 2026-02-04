package com.example.backend.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.dto.customer.CustomerHistoryDTO;
import com.example.backend.dto.customer.CustomerHistoryDTO.FavoriteItem;
import com.example.backend.dto.customer.CustomerHistoryDTO.OrderHistory;
import com.example.backend.dto.customer.CustomerHistoryDTO.OrderItemSummary;
import com.example.backend.model.CustomerEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.OrderItemEntity;
import com.example.backend.model.PaymentEntity;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;

@Service
public class CustomerHistoryService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public CustomerHistoryService(CustomerRepository customerRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public CustomerHistoryDTO getCustomerHistory(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        return buildCustomerHistory(customer);
    }

    public CustomerHistoryDTO getCustomerHistoryByPhone(String phone) {
        CustomerEntity customer = customerRepository.findAll().stream()
                .filter(c -> phone.equals(c.getPhone()) && c.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + phone));

        return buildCustomerHistory(customer);
    }

    private CustomerHistoryDTO buildCustomerHistory(CustomerEntity customer) {
        CustomerHistoryDTO history = new CustomerHistoryDTO();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Basic customer info
        history.setCustomerId(customer.getCustomerId());
        history.setCustomerName(customer.getName());
        history.setPhone(customer.getPhone());
        history.setLoyaltyPoints(customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0);

        if (customer.getCreatedAt() != null) {
            history.setMemberSince(customer.getCreatedAt().format(dateFormatter));
        }

        // Get all orders for this customer
        List<OrderEntity> customerOrders = orderRepository.findAll().stream()
                .filter(o -> o.getCustomer() != null &&
                        o.getCustomer().getCustomerId().equals(customer.getCustomerId()) &&
                        o.getDeletedAt() == null)
                .sorted((a, b) -> {
                    if (b.getCreatedAt() == null)
                        return -1;
                    if (a.getCreatedAt() == null)
                        return 1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());

        // Stats
        history.setTotalOrders(customerOrders.size());

        Double totalSpent = customerOrders.stream()
                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();
        history.setTotalSpent(totalSpent);

        // Last visit
        if (!customerOrders.isEmpty() && customerOrders.get(0).getCreatedAt() != null) {
            history.setLastVisit(customerOrders.get(0).getCreatedAt().format(dateFormatter));
        }

        // Recent orders (last 20)
        List<OrderHistory> recentOrders = new ArrayList<>();
        for (OrderEntity order : customerOrders.stream().limit(20).collect(Collectors.toList())) {
            OrderHistory oh = new OrderHistory();
            oh.setOrderId(order.getOrderId());
            oh.setOrderNo(order.getOrderNo());

            if (order.getCreatedAt() != null) {
                oh.setDate(order.getCreatedAt().format(dateFormatter));
                oh.setTime(order.getCreatedAt().format(timeFormatter));
            }

            oh.setOrderType(order.getOrderType() != null ? order.getOrderType().name() : "N/A");
            oh.setTotal(order.getTotalAmount());
            oh.setStatus(order.getStatus() != null ? order.getStatus().name() : "UNKNOWN");

            // Get payment method
            PaymentEntity payment = paymentRepository.findAll().stream()
                    .filter(p -> p.getOrder() != null &&
                            p.getOrder().getOrderId().equals(order.getOrderId()) &&
                            p.getDeletedAt() == null)
                    .findFirst()
                    .orElse(null);

            if (payment != null && payment.getMethod() != null) {
                oh.setPaymentMethod(payment.getMethod().name());
            }

            // Order items summary
            if (order.getItems() != null) {
                List<OrderItemSummary> items = order.getItems().stream()
                        .map(item -> new OrderItemSummary(
                                item.getMenuItem() != null ? item.getMenuItem().getName() : "Unknown",
                                item.getQty(),
                                item.getUnitPrice()))
                        .collect(Collectors.toList());
                oh.setItems(items);
            }

            recentOrders.add(oh);
        }
        history.setRecentOrders(recentOrders);

        // Favorite items (most ordered)
        Map<Long, FavoriteItem> itemMap = new HashMap<>();
        for (OrderEntity order : customerOrders) {
            if (order.getItems() != null && order.getStatus() == OrderEntity.OrderStatus.PAID) {
                for (OrderItemEntity item : order.getItems()) {
                    if (item.getMenuItem() != null) {
                        Long menuItemId = item.getMenuItem().getMenuItemId();
                        String name = item.getMenuItem().getName();

                        FavoriteItem fi = itemMap.getOrDefault(menuItemId, new FavoriteItem(menuItemId, name, 0, 0.0));
                        fi.setOrderCount(fi.getOrderCount() + (item.getQty() != null ? item.getQty() : 0));
                        fi.setTotalSpent(fi.getTotalSpent() + (item.getUnitPrice() != null && item.getQty() != null
                                ? item.getUnitPrice() * item.getQty()
                                : 0.0));
                        itemMap.put(menuItemId, fi);
                    }
                }
            }
        }

        List<FavoriteItem> favorites = itemMap.values().stream()
                .sorted((a, b) -> b.getOrderCount().compareTo(a.getOrderCount()))
                .limit(5)
                .collect(Collectors.toList());
        history.setFavoriteItems(favorites);

        return history;
    }
}
