package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.StockInRequestDTO;
import com.example.backend.dto.StockInResponseDTO;
import com.example.backend.services.StockInService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-in")
@CrossOrigin(origins = "http://localhost:8082")
@RequiredArgsConstructor
public class StockInController {

    private final StockInService stockInService;

    @PostMapping
    public ResponseEntity<StockInResponseDTO> recordStockIn(@Valid @RequestBody StockInRequestDTO request) {
        StockInResponseDTO response = stockInService.recordStockIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StockInResponseDTO>> getAllStockIns() {
        List<StockInResponseDTO> stockIns = stockInService.getAllStockInRecords();
        return ResponseEntity.ok(stockIns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockInResponseDTO> getStockInById(@PathVariable Long id) {
        StockInResponseDTO stockIn = stockInService.getStockInById(id);
        return ResponseEntity.ok(stockIn);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockInResponseDTO> updateStockIn(@PathVariable Long id,
            @Valid @RequestBody StockInRequestDTO request) {
        StockInResponseDTO updatedStockIn = stockInService.updateStockIn(id, request);
        return ResponseEntity.ok(updatedStockIn);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockIn(@PathVariable Long id) {
        stockInService.deleteStockIn(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<StockInResponseDTO>> getStockInsByIngredientId(@PathVariable Long ingredientId) {
        List<StockInResponseDTO> stockIns = stockInService.getStockInByIngredient(ingredientId);
        return ResponseEntity.ok(stockIns);
    }
}