package com.ws101.obrino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Product entity representing an e-commerce product in the database.
 *
 * This JPA entity maps to the 'products' table and encapsulates product information
 * including pricing, inventory, and categorization. Maintains a Many-to-One relationship
 * with the Category entity.
 *
 * @author Obrino
 * @version 2.0
 * @see Category
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_product_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * Auto-generated using database auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product.
     * Required field with minimum length validation at the service layer.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Detailed description of the product.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * The price of the product.
     * Must be a positive number. Stored with 2 decimal places.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Many-to-One relationship with Category.
     * Multiple products can belong to one category.
     *
     * @JoinColumn specifies the foreign key column name in the products table.
     * fetch = FetchType.LAZY improves performance by loading category only when accessed.
     * optional = false makes category mandatory for every product.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    /**
     * The number of units available in stock.
     * Must be non-negative.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * URL to the product image.
     * Optional field.
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * Timestamp when the product was created.
     * Automatically set to current time when the product is persisted.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated.
     * Automatically updated whenever the product is modified.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback executed before the entity is persisted.
     * Sets the creation and update timestamps.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA lifecycle callback executed before the entity is updated.
     * Updates the modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
