package com.ws101.obrino.repository;

import com.ws101.obrino.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Order entity.
 *
 * Extends JpaRepository to provide CRUD operations and custom query methods
 * for the Order entity.
 *
 * @author Obrino
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders by customer email.
     *
     * @param customerEmail the customer email address
     * @return a list of orders for the specified customer
     */
    List<Order> findByCustomerEmail(String customerEmail);

    /**
     * Find all orders by customer name.
     *
     * @param customerName the customer name
     * @return a list of orders for the specified customer
     */
    List<Order> findByCustomerName(String customerName);

    /**
     * Find all orders by status.
     *
     * @param status the order status
     * @return a list of orders with the specified status
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByStatus(@Param("status") Order.OrderStatus status);

    /**
     * Find the top N most recent orders.
     *
     * @param limit the number of orders to retrieve
     * @return a list of the most recent orders
     */
    @Query(value = "SELECT * FROM orders ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Order> findRecentOrders(@Param("limit") int limit);
}
