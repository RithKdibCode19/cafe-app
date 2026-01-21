package com.example.backend.dto.pos;

public class Cashier {

    private Long id;
    private String name;
    private String shift;
    private double salesTotal;
    private int transactionsCount;

    public Cashier() {
    }

    public Cashier(Long id, String name, String shift, double salesTotal, int transactionsCount) {
        this.id = id;
        this.name = name;
        this.shift = shift;
        this.salesTotal = salesTotal;
        this.transactionsCount = transactionsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public double getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(double salesTotal) {
        this.salesTotal = salesTotal;
    }

    public int getTransactionsCount() {
        return transactionsCount;
    }

    public void setTransactionsCount(int transactionsCount) {
        this.transactionsCount = transactionsCount;
    }
}
