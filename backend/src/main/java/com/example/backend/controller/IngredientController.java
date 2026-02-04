package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.IngredientRequestDTO;
import com.example.backend.dto.IngredientResponseDTO;
import com.example.backend.services.IngredientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<IngredientResponseDTO> createIngredient(@Valid @RequestBody IngredientRequestDTO request) {
        IngredientResponseDTO response = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponseDTO>> getAllIngredients() {
        List<IngredientResponseDTO> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> getIngredientById(@PathVariable Long id) {
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(@PathVariable Long id,
            @Valid @RequestBody IngredientRequestDTO request) {
        IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<IngredientResponseDTO>> searchIngredientsByName(@RequestParam String name) {
        List<IngredientResponseDTO> ingredients = ingredientService.searchIngredientsByName(name);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<IngredientResponseDTO>> getLowStockIngredients() {
        List<IngredientResponseDTO> ingredients = ingredientService.getLowStockIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<IngredientResponseDTO> getIngredientBySku(@PathVariable String sku) {
        IngredientResponseDTO ingredient = ingredientService.getIngredientBySku(sku);
        return ResponseEntity.ok(ingredient);
    }
}