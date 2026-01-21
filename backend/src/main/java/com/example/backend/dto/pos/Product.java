package com.example.backend.dto.pos;

public class Product {
    private Long id;
    private String name;
    private String sku;
    private String category;
    private double price;
    private int stock;
    private int sold;

    public Product() {}

    public Product(Long id, String name, String sku, String category, double price, int stock, int sold) {
        this.id = id; this.name = name; this.sku = sku; this.category = category; this.price = price; this.stock = stock; this.sold = sold;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }
}
