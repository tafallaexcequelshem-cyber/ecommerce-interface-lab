package com.ws101.obrino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * Category entity representing a product category in the e-commerce system.
 *
 * This entity represents product categories (e.g., Oversized, Standard Round Neck).
 * It maintains a One-to-Many relationship with Product entities.
 *
 * @author Obrino
 * @version 2.0
 * @see Product
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Unique identifier for the category.
     * Auto-generated using database auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the category.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Optional description of the category.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * One-to-Many relationship with Product.
     * One category can have many products.
     *
     * cascade = CascadeType.ALL: When a category is deleted, all associated products are deleted.
     * fetch = FetchType.LAZY: Products are loaded only when accessed (better performance).
     * orphanRemoval = true: If a product is removed from the list, it is automatically deleted from the database.
     */
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Product> products;

    /**
     * Constructor with name and description.
     * Useful for creating categories without the product list.
     *
     * @param name the category name
     * @param description the category description
     */
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
