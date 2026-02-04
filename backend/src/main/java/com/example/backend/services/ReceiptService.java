package com.example.backend.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.dto.report.ReceiptDTO;
import com.example.backend.dto.report.ReceiptDTO.ReceiptItem;
import com.example.backend.model.AddOnEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.model.OrderItemEntity;
import com.example.backend.model.PaymentEntity;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;

@Service
public class ReceiptService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public ReceiptService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public ReceiptDTO generateReceipt(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        return buildReceipt(order);
    }

    public ReceiptDTO generateReceiptByOrderNo(String orderNo) {
        OrderEntity order = orderRepository.findAll().stream()
                .filter(o -> orderNo.equals(o.getOrderNo()) && o.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found with Order No: " + orderNo));

        return buildReceipt(order);
    }

    private ReceiptDTO buildReceipt(OrderEntity order) {
        ReceiptDTO receipt = new ReceiptDTO();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Order Info
        receipt.setOrderNo(order.getOrderNo());
        receipt.setOrderType(order.getOrderType() != null ? formatOrderType(order.getOrderType().name()) : "N/A");

        if (order.getCreatedAt() != null) {
            receipt.setOrderDate(order.getCreatedAt().format(dateFormat));
            receipt.setOrderTime(order.getCreatedAt().format(timeFormat));
        }

        // Branch Info
        if (order.getBranch() != null) {
            receipt.setBranchName(order.getBranch().getName());
            receipt.setBranchPhone(order.getBranch().getPhone());
            receipt.setBranchAddress(order.getBranch().getLocation());
        }

        // Cashier Info
        if (order.getCashierUser() != null && order.getCashierUser().getEmployee() != null) {
            receipt.setCashierName(order.getCashierUser().getEmployee().getFullName());
        } else if (order.getCashierUser() != null) {
            receipt.setCashierName(order.getCashierUser().getUserName());
        }

        // Customer Info
        if (order.getCustomer() != null) {
            receipt.setCustomerName(order.getCustomer().getName());
        } else {
            receipt.setCustomerName("Walk-in Customer");
        }

        // Order Items
        List<ReceiptItem> items = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItemEntity orderItem : order.getItems()) {
                ReceiptItem item = new ReceiptItem();

                if (orderItem.getMenuItem() != null) {
                    item.setName(orderItem.getMenuItem().getName());
                }

                if (orderItem.getVariant() != null) {
                    item.setVariant(
                            orderItem.getVariant().getSize() != null ? orderItem.getVariant().getSize().name() : null);
                }

                item.setQty(orderItem.getQty());
                item.setUnitPrice(orderItem.getUnitPrice());
                item.setLineTotal(orderItem.getQty() != null && orderItem.getUnitPrice() != null
                        ? orderItem.getQty() * orderItem.getUnitPrice()
                        : 0.0);

                // Addons
                if (orderItem.getAddOnItems() != null && !orderItem.getAddOnItems().isEmpty()) {
                    String addonNames = orderItem.getAddOnItems().stream()
                            .map(AddOnEntity::getName)
                            .collect(Collectors.joining(", "));
                    item.setAddons(addonNames);
                }

                items.add(item);
            }
        }
        receipt.setItems(items);

        // Totals
        receipt.setSubTotal(order.getSubTotal() != null ? order.getSubTotal() : 0.0);
        receipt.setDiscountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0);
        receipt.setTaxAmount(order.getTaxAmount() != null ? order.getTaxAmount() : 0.0);
        receipt.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);

        // Payment Info
        PaymentEntity payment = paymentRepository.findAll().stream()
                .filter(p -> p.getOrder() != null &&
                        p.getOrder().getOrderId().equals(order.getOrderId()) &&
                        p.getDeletedAt() == null)
                .findFirst()
                .orElse(null);

        if (payment != null) {
            receipt.setPaymentMethod(payment.getMethod() != null ? payment.getMethod().name() : "UNKNOWN");
            receipt.setAmountPaid(payment.getPaidAmount());
            receipt.setChangeAmount(payment.getPaidAmount() != null && order.getTotalAmount() != null
                    ? Math.max(0, payment.getPaidAmount() - order.getTotalAmount())
                    : 0.0);
        }

        // Footer
        receipt.setFooterMessage("Thank you for your visit! Please come again.");

        return receipt;
    }

    private String formatOrderType(String type) {
        switch (type) {
            case "DINE_IN":
                return "Dine In";
            case "TAKEAWAY":
                return "Takeaway";
            case "DELIVERY":
                return "Delivery";
            default:
                return type;
        }
    }
}
