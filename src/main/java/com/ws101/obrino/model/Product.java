package com.ws101.obrino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product entity representing an e-commerce product.
 *
 * This class encapsulates product information including pricing, inventory,
 * and categorization. Lombok annotations are used to automatically generate
 * getters, setters, constructors, toString, equals, and hashCode methods.
 *
 * @author Obrino
 * @version 1.0
 * @see com.ws101.obrino.service.ProductService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     */
    private Long id;

    /**
     * The name of the product.
     * Required field with minimum length validation.
     */
    private String name;

    /**
     * Detailed description of the product.
     */
    private String description;

    /**
     * The price of the product.
     * Must be a positive number.
     */
    private Double price;

    /**
     * The category to which the product belongs.
     * Required field for filtering and organization.
     */
    private String category;

    /**
     * The number of units available in stock.
     * Must be non-negative.
     */
    private Integer stock;

    /**
     * URL to the product image.
     * Optional field.
     */
    private String imageUrl;
}
