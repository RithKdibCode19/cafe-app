package com.example.backend.dto.mobile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MobileMenuDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchInfo {
        private Long branchId;
        private String name;
        private String code;
        private String phone;
        private String location;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long categoryId;
        private String name;
        private String description;
        private Long parentId;
        private List<CategoryInfo> children;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemInfo {
        private Long menuItemId;
        private String name;
        private Double basePrice;
        private String imageUrl;
        private Boolean isAvailable;
        private Long categoryId;
        private String categoryName;
        private List<VariantInfo> variants;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantInfo {
        private Long variantId;
        private String size; // S, M, L
        private Double price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddOnInfo {
        private Long addOnId;
        private String name;
        private Double price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItemDetail {
        private Long menuItemId;
        private String name;
        private String description;
        private Double basePrice;
        private String imageUrl;
        private Boolean isAvailable;
        private String categoryName;
        private List<VariantInfo> variants;
        private List<AddOnInfo> addOns;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchMenuResponse {
        private BranchInfo branch;
        private List<CategoryWithItems> categories;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryWithItems {
        private Long categoryId;
        private String name;
        private List<MenuItemInfo> items;
    }
}
