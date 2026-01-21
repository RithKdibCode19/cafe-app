package com.example.backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.pos.Cashier;
import com.example.backend.dto.pos.Product;
import com.example.backend.dto.pos.SaleItem;
import com.example.backend.dto.pos.SalesRecord;

@RestController
@RequestMapping("/api/pos")
public class POSController {

    @GetMapping(value = "/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesRecord> getSales() {
        List<SaleItem> items1 = Arrays.asList(new SaleItem(1L, "Espresso", 1, 2.5), new SaleItem(2L, "Latte", 1, 3.5));
        List<SaleItem> items2 = Arrays.asList(new SaleItem(3L, "Bagel", 2, 1.5));

        SalesRecord r1 = new SalesRecord(1001L, "2026-01-21T09:12:00", 6.0, items1, "CARD", 11L, "COMPLETED");
        SalesRecord r2 = new SalesRecord(1002L, "2026-01-21T09:18:00", 3.0, items2, "CASH", 12L, "COMPLETED");

        return Arrays.asList(r1, r2);
    }

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> getProducts() {
        Product p1 = new Product(1L, "Espresso", "ESP-001", "Beverages", 2.5, 120, 34);
        Product p2 = new Product(2L, "Latte", "LAT-001", "Beverages", 3.5, 80, 21);
        Product p3 = new Product(3L, "Bagel", "BG-001", "Food", 1.5, 12, 14);
        return Arrays.asList(p1, p2, p3);
    }

    @GetMapping(value = "/cashiers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Cashier> getCashiers() {
        Cashier c1 = new Cashier(11L, "Alice", "Morning", 120.5, 34);
        Cashier c2 = new Cashier(12L, "Bob", "Morning", 98.0, 21);
        List<Cashier> cs = new ArrayList<>(); cs.add(c1); cs.add(c2); return cs;
    }
}
