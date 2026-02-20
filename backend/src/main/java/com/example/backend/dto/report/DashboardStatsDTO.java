package com.example.backend.dto.report;

import java.util.List;

public class DashboardStatsDTO {

    private Double todayRevenue;
    private Double todayExpenses;
    private Double todayNetProfit;
    private Integer todayOrderCount;
    private Double averageOrderValue;
    private Integer todayCustomerCount;

    private List<PaymentMethodBreakdown> paymentBreakdown;
    private List<TopSellingItem> topSellingItems;
    private List<RecentOrder> recentOrders;
    private List<DailySales> dailySales;

    // Nested DTOs
    public static class PaymentMethodBreakdown {
        private String method;
        private Double amount;
        private Integer count;

        public PaymentMethodBreakdown() {
        }

        public PaymentMethodBreakdown(String method, Double amount, Integer count) {
            this.method = method;
            this.amount = amount;
            this.count = count;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    public static class TopSellingItem {
        private Long menuItemId;
        private String name;
        private Integer quantitySold;
        private Double revenue;

        public TopSellingItem() {
        }

        public TopSellingItem(Long menuItemId, String name, Integer quantitySold, Double revenue) {
            this.menuItemId = menuItemId;
            this.name = name;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
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

        public Integer getQuantitySold() {
            return quantitySold;
        }

        public void setQuantitySold(Integer quantitySold) {
            this.quantitySold = quantitySold;
        }

        public Double getRevenue() {
            return revenue;
        }

        public void setRevenue(Double revenue) {
            this.revenue = revenue;
        }
    }

    public static class RecentOrder {
        private Long orderId;
        private String orderNo;
        private Double total;
        private String status;
        private String customerName;
        private String createdAt;

        public RecentOrder() {
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

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class DailySales {
        private String date;
        private Double revenue;
        private Integer orderCount;

        public DailySales() {
        }

        public DailySales(String date, Double revenue, Integer orderCount) {
            this.date = date;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Double getRevenue() {
            return revenue;
        }

        public void setRevenue(Double revenue) {
            this.revenue = revenue;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }
    }

    // Main DTO Getters/Setters
    public Double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(Double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public Double getTodayExpenses() {
        return todayExpenses;
    }

    public void setTodayExpenses(Double todayExpenses) {
        this.todayExpenses = todayExpenses;
    }

    public Double getTodayNetProfit() {
        return todayNetProfit;
    }

    public void setTodayNetProfit(Double todayNetProfit) {
        this.todayNetProfit = todayNetProfit;
    }

    public Integer getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Integer todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public Double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(Double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public Integer getTodayCustomerCount() {
        return todayCustomerCount;
    }

    public void setTodayCustomerCount(Integer todayCustomerCount) {
        this.todayCustomerCount = todayCustomerCount;
    }

    public List<PaymentMethodBreakdown> getPaymentBreakdown() {
        return paymentBreakdown;
    }

    public void setPaymentBreakdown(List<PaymentMethodBreakdown> paymentBreakdown) {
        this.paymentBreakdown = paymentBreakdown;
    }

    public List<TopSellingItem> getTopSellingItems() {
        return topSellingItems;
    }

    public void setTopSellingItems(List<TopSellingItem> topSellingItems) {
        this.topSellingItems = topSellingItems;
    }

    public List<RecentOrder> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<RecentOrder> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<DailySales> getDailySales() {
        return dailySales;
    }

    public void setDailySales(List<DailySales> dailySales) {
        this.dailySales = dailySales;
    }
}
