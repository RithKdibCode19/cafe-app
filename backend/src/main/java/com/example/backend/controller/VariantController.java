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
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.VariantRequestDTO;
import com.example.backend.dto.VariantResponseDTO;
import com.example.backend.services.VariantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/variants")
@CrossOrigin(origins = "http://localhost:8082")
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @PostMapping
    public ResponseEntity<VariantResponseDTO> createVariant(@Valid @RequestBody VariantRequestDTO request) {
        VariantResponseDTO response = variantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VariantResponseDTO>> getAllVariants() {
        List<VariantResponseDTO> variants = variantService.getAllVariants();
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VariantResponseDTO> getVariantById(@PathVariable Long id) {
        VariantResponseDTO variant = variantService.getVariantById(id);
        return ResponseEntity.ok(variant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariantResponseDTO> updateVariant(@PathVariable Long id,
            @Valid @RequestBody VariantRequestDTO request) {
        VariantResponseDTO updatedVariant = variantService.updateVariant(id, request);
        return ResponseEntity.ok(updatedVariant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<VariantResponseDTO>> getVariantsByMenuItem(@PathVariable Long menuItemId) {
        List<VariantResponseDTO> variants = variantService.getVariantsByMenuItemId(menuItemId);
        return ResponseEntity.ok(variants);
    }
}