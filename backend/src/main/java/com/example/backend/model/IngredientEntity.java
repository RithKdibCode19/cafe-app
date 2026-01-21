package com.example.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblingredients", indexes = {
        @Index(name = "idx_ingredient_sku", columnList = "sku")
})
public class IngredientEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientUnit unit; // g / ml / pcs

    @Column(name = "reorder_level", nullable = false)
    private Double reorderLevel;

    @Column(name = "current_stock", nullable = false)
    private Double currentStock;

    @Column(name = "cost_per_unit", nullable = false)
    private Double costPerUnit;

    public enum IngredientUnit {
        G,
        ML,
        PCS
    }

}
