package com.example.backend.dto.customer;

import java.util.List;

public class CustomerHistoryDTO {

    private Long customerId;
    private String customerName;
    private String phone;
    private Integer totalOrders;
    private Double totalSpent;
    private Integer loyaltyPoints;
    private String memberSince;
    private String lastVisit;

    private List<OrderHistory> recentOrders;
    private List<FavoriteItem> favoriteItems;

    // Nested DTOs
    public static class OrderHistory {
        private Long orderId;
        private String orderNo;
        private String date;
        private String time;
        private String orderType;
        private Double total;
        private String status;
        private String paymentMethod;
        private List<OrderItemSummary> items;

        public OrderHistory() {
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public List<OrderItemSummary> getItems() {
            return items;
        }

        public void setItems(List<OrderItemSummary> items) {
            this.items = items;
        }
    }

    public static class OrderItemSummary {
        private String name;
        private Integer qty;
        private Double price;

        public OrderItemSummary() {
        }

        public OrderItemSummary(String name, Integer qty, Double price) {
            this.name = name;
            this.qty = qty;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

    public static class FavoriteItem {
        private Long menuItemId;
        private String name;
        private Integer orderCount;
        private Double totalSpent;

        public FavoriteItem() {
        }

        public FavoriteItem(Long menuItemId, String name, Integer orderCount, Double totalSpent) {
            this.menuItemId = menuItemId;
            this.name = name;
            this.orderCount = orderCount;
            this.totalSpent = totalSpent;
        }

        public Long getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(Long menuItemId) {
            this.menuItemId = menuItemId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }

        public Double getTotalSpent() {
            return totalSpent;
        }

        public void setTotalSpent(Double totalSpent) {
            this.totalSpent = totalSpent;
        }
    }

    // Main DTO Getters/Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public String getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(String lastVisit) {
        this.lastVisit = lastVisit;
    }

    public List<OrderHistory> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<OrderHistory> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<FavoriteItem> getFavoriteItems() {
        return favoriteItems;
    }

    public void setFavoriteItems(List<FavoriteItem> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }
}
