package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.services.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{menuItemId}")
    public ResponseEntity<List<MenuItemResponseDTO>> getRecommendations(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(recommendationService.getFrequentlyBoughtWith(menuItemId));
    }
}
