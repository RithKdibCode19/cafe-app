package com.example.backend.controller.publicapi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.mobile.MobileMenuDTO;
import com.example.backend.model.AddOnEntity;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.repository.AddOnRepository;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MenuItemRepository;

/**
 * Public menu browsing endpoints (no auth required).
 * Used by QR code web menu for in-store customers.
 */
@RestController
@RequestMapping("/api/public")
public class PublicMenuController {

    private final BranchRepository branchRepository;
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final AddOnRepository addOnRepository;

    public PublicMenuController(
            BranchRepository branchRepository,
            MenuItemRepository menuItemRepository,
            CategoryRepository categoryRepository,
            AddOnRepository addOnRepository) {
        this.branchRepository = branchRepository;
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.addOnRepository = addOnRepository;
    }

    /**
     * Get full menu for a branch by its code (used in QR URL).
     * GET /api/public/branches/{branchCode}/menu
     */
    @GetMapping("/branches/{branchCode}/menu")
    public ResponseEntity<MobileMenuDTO.BranchMenuResponse> getBranchMenuByCode(
            @PathVariable String branchCode) {

        BranchEntity branch = branchRepository.findByCode(branchCode).orElse(null);
        if (branch == null || branch.getDeletedAt() != null) {
            return ResponseEntity.notFound().build();
        }

        // Get all active menu items
        List<MenuItemEntity> menuItems = menuItemRepository.findAllActive();

        // Get all root categories
        List<CategoryEntity> rootCategories = categoryRepository.findRoots();

        // Build categories with their items
        List<MobileMenuDTO.CategoryWithItems> categoryWithItems = new ArrayList<>();
        for (CategoryEntity cat : rootCategories) {
            List<MobileMenuDTO.MenuItemInfo> items = menuItems.stream()
                    .filter(item -> item.getIsAvailable() != null && item.getIsAvailable())
                    .filter(item -> item.getCategory() != null &&
                            item.getCategory().getCategoryId().equals(cat.getCategoryId()))
                    .map(this::toMenuItemInfo)
                    .collect(Collectors.toList());

            // Include sub-categories' items too
            if (cat.getChildren() != null) {
                for (CategoryEntity child : cat.getChildren()) {
                    List<MobileMenuDTO.MenuItemInfo> childItems = menuItems.stream()
                            .filter(item -> item.getIsAvailable() != null && item.getIsAvailable())
                            .filter(item -> item.getCategory() != null &&
                                    item.getCategory().getCategoryId().equals(child.getCategoryId()))
                            .map(this::toMenuItemInfo)
                            .collect(Collectors.toList());
                    items.addAll(childItems);
                }
            }

            if (!items.isEmpty()) {
                categoryWithItems.add(MobileMenuDTO.CategoryWithItems.builder()
                        .categoryId(cat.getCategoryId())
                        .name(cat.getName())
                        .nameKh(cat.getNameKh())
                        .items(items)
                        .build());
            }
        }

        MobileMenuDTO.BranchMenuResponse response = MobileMenuDTO.BranchMenuResponse.builder()
                .branch(toBranchInfo(branch))
                .categories(categoryWithItems)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get branch info by code.
     * GET /api/public/branches/{branchCode}
     */
    @GetMapping("/branches/{branchCode}")
    public ResponseEntity<MobileMenuDTO.BranchInfo> getBranchByCode(
            @PathVariable String branchCode) {
        BranchEntity branch = branchRepository.findByCode(branchCode).orElse(null);
        if (branch == null || branch.getDeletedAt() != null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toBranchInfo(branch));
    }

    /**
     * Get single menu item details (with variants & add-ons).
     * GET /api/public/menu/{menuItemId}
     */
    @GetMapping("/menu/{menuItemId}")
    public ResponseEntity<MobileMenuDTO.MenuItemDetail> getMenuItemDetail(
            @PathVariable Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .filter(item -> item.getDeletedAt() == null)
                .map(item -> {
                    List<AddOnEntity> allAddOns = addOnRepository.findAll();
                    return ResponseEntity.ok(toMenuItemDetail(item, allAddOns));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search menu items.
     * GET /api/public/menu/search?q=latte
     */
    @GetMapping("/menu/search")
    public ResponseEntity<List<MobileMenuDTO.MenuItemInfo>> searchMenu(
            @RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<MenuItemEntity> results = menuItemRepository.searchByName(q.trim());
        List<MobileMenuDTO.MenuItemInfo> items = results.stream()
                .filter(item -> item.getIsAvailable() != null && item.getIsAvailable())
                .map(this::toMenuItemInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    /**
     * Get all add-ons.
     * GET /api/public/addons
     */
    @GetMapping("/addons")
    public ResponseEntity<List<MobileMenuDTO.AddOnInfo>> getAllAddOns() {
        List<AddOnEntity> addOns = addOnRepository.findAll();
        List<MobileMenuDTO.AddOnInfo> result = addOns.stream()
                .map(this::toAddOnInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ========== Mappers ==========

    private MobileMenuDTO.BranchInfo toBranchInfo(BranchEntity branch) {
        return MobileMenuDTO.BranchInfo.builder()
                .branchId(branch.getBranchId())
                .name(branch.getName())
                .code(branch.getCode())
                .phone(branch.getPhone())
                .location(branch.getLocation())
                .latitude(branch.getLatitude())
                .longitude(branch.getLongitude())
                .build();
    }

    private MobileMenuDTO.MenuItemInfo toMenuItemInfo(MenuItemEntity item) {
        List<MobileMenuDTO.VariantInfo> variants = List.of();
        if (item.getVariants() != null) {
            variants = item.getVariants().stream()
                    .map(v -> MobileMenuDTO.VariantInfo.builder()
                            .variantId(v.getVariantId())
                            .size(v.getSize().name())
                            .price(v.getPrice())
                            .build())
                    .collect(Collectors.toList());
        }

        return MobileMenuDTO.MenuItemInfo.builder()
                .menuItemId(item.getMenuItemId())
                .name(item.getName())
                .nameKh(item.getNameKh())
                .basePrice(item.getBasePrice())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .categoryId(item.getCategory() != null ? item.getCategory().getCategoryId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .variants(variants)
                .build();
    }

    private MobileMenuDTO.MenuItemDetail toMenuItemDetail(MenuItemEntity item, List<AddOnEntity> addOns) {
        List<MobileMenuDTO.VariantInfo> variants = List.of();
        if (item.getVariants() != null) {
            variants = item.getVariants().stream()
                    .map(v -> MobileMenuDTO.VariantInfo.builder()
                            .variantId(v.getVariantId())
                            .size(v.getSize().name())
                            .price(v.getPrice())
                            .build())
                    .collect(Collectors.toList());
        }

        List<MobileMenuDTO.AddOnInfo> addOnInfos = addOns.stream()
                .map(this::toAddOnInfo)
                .collect(Collectors.toList());

        return MobileMenuDTO.MenuItemDetail.builder()
                .menuItemId(item.getMenuItemId())
                .name(item.getName())
                .nameKh(item.getNameKh())
                .description(item.getDescription())
                .descriptionKh(item.getDescriptionKh())
                .basePrice(item.getBasePrice())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .variants(variants)
                .addOns(addOnInfos)
                .build();
    }

    private MobileMenuDTO.AddOnInfo toAddOnInfo(AddOnEntity addOn) {
        return MobileMenuDTO.AddOnInfo.builder()
                .addOnId(addOn.getAddonId())
                .name(addOn.getName())
                .nameKh(addOn.getNameKh())
                .price(addOn.getPrice())
                .build();
    }
}
