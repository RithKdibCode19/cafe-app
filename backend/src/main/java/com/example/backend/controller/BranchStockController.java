package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.BranchStockEntity;
import com.example.backend.services.BranchStockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory/branch")
@RequiredArgsConstructor
public class BranchStockController {

    private final BranchStockService branchStockService;

    @GetMapping("/{branchId}")
    public ResponseEntity<List<BranchStockEntity>> getBranchInventory(@PathVariable Long branchId) {
        return ResponseEntity.ok(branchStockService.getBranchInventory(branchId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferStock(
            @RequestParam Long fromBranchId,
            @RequestParam Long toBranchId,
            @RequestParam Long ingredientId,
            @RequestParam Double amount) {
        branchStockService.transferStock(fromBranchId, toBranchId, ingredientId, amount);
        return ResponseEntity.ok("Transfer successful");
    }
}
