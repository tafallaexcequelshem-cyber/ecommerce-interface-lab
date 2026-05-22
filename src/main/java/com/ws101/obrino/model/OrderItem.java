package com.ws101.obrino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;

/**
 * OrderItem entity representing an individual item within an order.
 *
 * This entity represents a single product item added to an order with quantity and price.
 * It maintains a Many-to-One relationship with both Order and Product entities.
 *
 * @author Obrino
 * @version 1.0
 * @see Order
 * @see Product
 */
@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Unique identifier for the order item.
     * Auto-generated using database auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-One relationship with Order.
     * Multiple order items belong to one order.
     *
     * @JoinColumn specifies the foreign key column name.
     * fetch = FetchType.LAZY: Order is loaded only when accessed.
     * optional = false makes order mandatory for every order item.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

    /**
     * Many-to-One relationship with Product.
     * Multiple order items can reference the same product.
     *
     * fetch = FetchType.EAGER: Product is loaded immediately since we need product details.
     * optional = false makes product mandatory for every order item.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product in this order item.
     * Must be positive.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price per unit at the time of order.
     * This is stored to maintain price history (product price may change later).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private Double pricePerUnit;

    /**
     * Calculates and returns the total price for this order item.
     *
     * @return the product of quantity and pricePerUnit
     */
    public Double getTotalPrice() {
        return quantity * pricePerUnit;
    }
}
