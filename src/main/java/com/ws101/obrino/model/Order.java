package com.ws101.obrino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * Order entity representing a customer order in the e-commerce system.
 *
 * This entity represents a customer order containing one or more order items.
 * It maintains a One-to-Many relationship with OrderItem entities.
 *
 * @author Obrino
 * @version 1.0
 * @see OrderItem
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_date", columnList = "created_at"),
        @Index(name = "idx_order_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Unique identifier for the order.
     * Auto-generated using database auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer name for the order.
     */
    @Column(nullable = false)
    private String customerName;

    /**
     * Customer email address.
     */
    @Column(nullable = false)
    private String customerEmail;

    /**
     * Delivery address for the order.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    /**
     * Total order amount.
     * Calculated from the sum of all order items.
     */
    @Column(nullable = false)
    private BigDecimal totalAmount;

    /**
     * Status of the order (e.g., PENDING, PROCESSING, SHIPPED, DELIVERED).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * One-to-Many relationship with OrderItem.
     * One order can have many order items.
     *
     * cascade = CascadeType.ALL: When an order is deleted, all associated items are deleted.
     * fetch = FetchType.LAZY: Order items are loaded only when accessed.
     * orphanRemoval = true: If an item is removed from the list, it is automatically deleted.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<OrderItem> orderItems;

    /**
     * Timestamp when the order was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the order was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA lifecycle callback executed before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = OrderStatus.PENDING;
    }

    /**
     * JPA lifecycle callback executed before the entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enum representing possible order statuses.
     */
    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}
