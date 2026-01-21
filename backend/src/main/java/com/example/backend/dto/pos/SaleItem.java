package com.example.backend.dto.pos;

public class SaleItem {
    private Long productId;
    private String name;
    private int qty;
    private double price;

    public SaleItem() {}

    public SaleItem(Long productId, String name, int qty, double price) {
        this.productId = productId;
        this.name = name;
        this.qty = qty;
        this.price = price;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
