package com.example.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblrecipes", indexes = {
        @Index(name = "idx_recipe_menu_item_id", columnList = "menu_item_id")
})
public class RecipeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemEntity menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    @Column(name = "quantity_needed", nullable = false)
    private Double quantityNeeded; // Amount of ingredient used (e.g., 20.0 for 20g)

    // Manual Getters/Setters
    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public MenuItemEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemEntity menuItem) { this.menuItem = menuItem; }

    public IngredientEntity getIngredient() { return ingredient; }
    public void setIngredient(IngredientEntity ingredient) { this.ingredient = ingredient; }

    public Double getQuantityNeeded() { return quantityNeeded; }
    public void setQuantityNeeded(Double quantityNeeded) { this.quantityNeeded = quantityNeeded; }
}
