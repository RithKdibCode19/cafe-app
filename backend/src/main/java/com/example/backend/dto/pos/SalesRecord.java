package com.example.backend.dto.pos;

import java.util.List;

public class SalesRecord {
    private Long id;
    private String timestamp;
    private double total;
    private List<SaleItem> items;
    private String paymentType;
    private Long cashierId;
    private String status;

    public SalesRecord() {}

    public SalesRecord(Long id, String timestamp, double total, List<SaleItem> items, String paymentType, Long cashierId, String status) {
        this.id = id;
        this.timestamp = timestamp;
        this.total = total;
        this.items = items;
        this.paymentType = paymentType;
        this.cashierId = cashierId;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public Long getCashierId() { return cashierId; }
    public void setCashierId(Long cashierId) { this.cashierId = cashierId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
