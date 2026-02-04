package com.example.backend.dto.report;

import java.util.List;

public class SalesReportDTO {

    private String startDate;
    private String endDate;
    private Double totalRevenue;
    private Integer totalOrders;
    private Double averageOrderValue;
    private Integer totalItemsSold;

    private List<DailySales> dailySales;
    private List<PaymentMethodSummary> paymentMethodSummary;
    private List<CategorySales> categorySales;
    private List<TopSellingItem> topSellingItems;

    // Nested DTOs
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

    public static class PaymentMethodSummary {
        private String method;
        private Double amount;
        private Integer count;
        private Double percentage;

        public PaymentMethodSummary() {
        }

        public PaymentMethodSummary(String method, Double amount, Integer count, Double percentage) {
            this.method = method;
            this.amount = amount;
            this.count = count;
            this.percentage = percentage;
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

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }

    public static class CategorySales {
        private Long categoryId;
        private String categoryName;
        private Double revenue;
        private Integer itemsSold;

        public CategorySales() {
        }

        public CategorySales(Long categoryId, String categoryName, Double revenue, Integer itemsSold) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.revenue = revenue;
            this.itemsSold = itemsSold;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Double getRevenue() {
            return revenue;
        }

        public void setRevenue(Double revenue) {
            this.revenue = revenue;
        }

        public Integer getItemsSold() {
            return itemsSold;
        }

        public void setItemsSold(Integer itemsSold) {
            this.itemsSold = itemsSold;
        }
    }

    public static class TopSellingItem {
        private Long menuItemId;
        private String name;
        private String categoryName;
        private Integer quantitySold;
        private Double revenue;

        public TopSellingItem() {
        }

        public TopSellingItem(Long menuItemId, String name, String categoryName, Integer quantitySold, Double revenue) {
            this.menuItemId = menuItemId;
            this.name = name;
            this.categoryName = categoryName;
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

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
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

    // Main DTO Getters/Setters
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(Double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public Integer getTotalItemsSold() {
        return totalItemsSold;
    }

    public void setTotalItemsSold(Integer totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }

    public List<DailySales> getDailySales() {
        return dailySales;
    }

    public void setDailySales(List<DailySales> dailySales) {
        this.dailySales = dailySales;
    }

    public List<PaymentMethodSummary> getPaymentMethodSummary() {
        return paymentMethodSummary;
    }

    public void setPaymentMethodSummary(List<PaymentMethodSummary> paymentMethodSummary) {
        this.paymentMethodSummary = paymentMethodSummary;
    }

    public List<CategorySales> getCategorySales() {
        return categorySales;
    }

    public void setCategorySales(List<CategorySales> categorySales) {
        this.categorySales = categorySales;
    }

    public List<TopSellingItem> getTopSellingItems() {
        return topSellingItems;
    }

    public void setTopSellingItems(List<TopSellingItem> topSellingItems) {
        this.topSellingItems = topSellingItems;
    }
}
