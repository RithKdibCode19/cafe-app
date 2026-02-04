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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_order_items")
public class OrderItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemEntity menuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private VariantEntity variant; // size

    @Column(nullable = false)
    private Integer qty;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(columnDefinition = "TEXT")
    private String addons; // JSON or text note

    @jakarta.persistence.ManyToMany(fetch = FetchType.LAZY)
    @jakarta.persistence.JoinTable(name = "tbl_order_item_addons", joinColumns = @jakarta.persistence.JoinColumn(name = "order_item_id"), inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "addon_id"))
    private java.util.List<AddOnEntity> addOnItems;

    // Manual Getters/Setters
    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public MenuItemEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemEntity menuItem) { this.menuItem = menuItem; }

    public VariantEntity getVariant() { return variant; }
    public void setVariant(VariantEntity variant) { this.variant = variant; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public String getAddons() { return addons; }
    public void setAddons(String addons) { this.addons = addons; }

    public java.util.List<AddOnEntity> getAddOnItems() { return addOnItems; }
    public void setAddOnItems(java.util.List<AddOnEntity> addOnItems) { this.addOnItems = addOnItems; }
}
