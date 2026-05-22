package com.ws101.obrino.repository;

import com.ws101.obrino.model.OrderItem;
import com.ws101.obrino.model.Order;
import com.ws101.obrino.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for OrderItem entity.
 *
 * Extends JpaRepository to provide CRUD operations for the OrderItem entity.
 *
 * @author Obrino
 * @version 1.0
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all order items for a specific order.
     *
     * @param order the order entity
     * @return a list of order items in the specified order
     */
    List<OrderItem> findByOrder(Order order);

    /**
     * Find all order items for a specific product.
     *
     * @param product the product entity
     * @return a list of order items containing the specified product
     */
    List<OrderItem> findByProduct(Product product);
}
