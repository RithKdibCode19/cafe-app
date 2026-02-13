package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.MenuItemRequestDTO;
import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.services.MenuItemService;


@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(@RequestBody MenuItemRequestDTO request) {
        MenuItemResponseDTO response = menuItemService.createMenuItem(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @com.example.backend.security.IsolateByBranch
    public ResponseEntity<List<MenuItemResponseDTO>> getAllMenuItems(Long branchId) {
        List<MenuItemResponseDTO> response = menuItemService.getAllMenuItems(branchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItemById(@PathVariable Long id) {
        MenuItemResponseDTO response = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> updateMenuItem(@PathVariable Long id,
            @RequestBody MenuItemRequestDTO request) {
        MenuItemResponseDTO response = menuItemService.updateMenuItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok().build();
    }
}
