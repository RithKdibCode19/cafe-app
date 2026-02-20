package com.example.backend.controller.mobile;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/mobile")
@Tag(name = "Mobile - Menu", description = "Public menu browsing endpoints for mobile customers (no auth required)")
public class MobileMenuController {

    private final BranchRepository branchRepository;
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final AddOnRepository addOnRepository;

    public MobileMenuController(
            BranchRepository branchRepository,
            MenuItemRepository menuItemRepository,
            CategoryRepository categoryRepository,
            AddOnRepository addOnRepository) {
        this.branchRepository = branchRepository;
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.addOnRepository = addOnRepository;
    }

    // ========== Branches ==========

    @GetMapping("/branches")
    @Operation(summary = "List all branches", description = "Returns all active branches with location info")
    public ResponseEntity<List<MobileMenuDTO.BranchInfo>> getAllBranches() {
        List<BranchEntity> branches = branchRepository.findAllActiveBranches();
        List<MobileMenuDTO.BranchInfo> result = branches.stream()
                .map(this::toBranchInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/branches/{branchId}")
    @Operation(summary = "Get branch details", description = "Returns a single branch by ID")
    public ResponseEntity<MobileMenuDTO.BranchInfo> getBranchById(
            @Parameter(description = "Branch ID") @PathVariable Long branchId) {
        BranchEntity branch = branchRepository.findActiveBranchById(branchId);
        if (branch == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toBranchInfo(branch));
    }

    // ========== Menu ==========

    @GetMapping("/branches/{branchId}/menu")
    @Operation(summary = "Get full menu for a branch",
            description = "Returns the entire menu organized by categories with items, variants, and add-ons")
    public ResponseEntity<MobileMenuDTO.BranchMenuResponse> getBranchMenu(
            @Parameter(description = "Branch ID") @PathVariable Long branchId) {

        BranchEntity branch = branchRepository.findActiveBranchById(branchId);
        if (branch == null) {
            return ResponseEntity.notFound().build();
        }

        // Get all active menu items
        List<MenuItemEntity> menuItems = menuItemRepository.findAllActive();

        // Get all root categories
        List<CategoryEntity> rootCategories = categoryRepository.findRoots();

        // Get all add-ons (available globally)
        List<AddOnEntity> allAddOns = addOnRepository.findAll();

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

    @GetMapping("/menu/search")
    @Operation(summary = "Search menu items", description = "Search menu items by name (case-insensitive)")
    public ResponseEntity<List<MobileMenuDTO.MenuItemInfo>> searchMenu(
            @Parameter(description = "Search query") @RequestParam String q) {
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

    @GetMapping("/menu/{menuItemId}")
    @Operation(summary = "Get menu item detail", description = "Returns full details including variants and available add-ons")
    public ResponseEntity<MobileMenuDTO.MenuItemDetail> getMenuItemDetail(
            @Parameter(description = "Menu Item ID") @PathVariable Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .filter(item -> item.getDeletedAt() == null)
                .map(item -> {
                    List<AddOnEntity> allAddOns = addOnRepository.findAll();
                    return ResponseEntity.ok(toMenuItemDetail(item, allAddOns));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    @Operation(summary = "List all categories", description = "Returns all categories with hierarchy")
    public ResponseEntity<List<MobileMenuDTO.CategoryInfo>> getAllCategories() {
        List<CategoryEntity> roots = categoryRepository.findRoots();
        List<MobileMenuDTO.CategoryInfo> result = roots.stream()
                .map(this::toCategoryInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/addons")
    @Operation(summary = "List all add-ons", description = "Returns all available add-ons with prices")
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
                .description(item.getDescription())
                .basePrice(item.getBasePrice())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .variants(variants)
                .addOns(addOnInfos)
                .build();
    }

    private MobileMenuDTO.CategoryInfo toCategoryInfo(CategoryEntity category) {
        List<MobileMenuDTO.CategoryInfo> children = List.of();
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            children = category.getChildren().stream()
                    .map(this::toCategoryInfo)
                    .collect(Collectors.toList());
        }

        return MobileMenuDTO.CategoryInfo.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getCategoryId() : null)
                .children(children)
                .build();
    }

    private MobileMenuDTO.AddOnInfo toAddOnInfo(AddOnEntity addOn) {
        return MobileMenuDTO.AddOnInfo.builder()
                .addOnId(addOn.getAddonId())
                .name(addOn.getName())
                .price(addOn.getPrice())
                .build();
    }
}
