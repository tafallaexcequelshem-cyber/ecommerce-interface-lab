package com.ws101.obrino.repository;

import com.ws101.obrino.model.Product;
import com.ws101.obrino.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigDecimal;

/**
 * Repository interface for Product entity.
 *
 * Extends JpaRepository to provide CRUD operations and custom query methods
 * for the Product entity.
 *
 * @author Obrino
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products belonging to a specific category.
     *
     * @param category the category to filter by
     * @return a list of products in the specified category
     */
    List<Product> findByCategory(Category category);

    /**
     * Find all products by category name.
     *
     * @param categoryName the name of the category
     * @return a list of products in the specified category
     */
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Find products by name (partial match, case-insensitive).
     *
     * @param name the product name or partial name
     * @return a list of products matching the name
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products within a specific price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return a list of products within the price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find all products in stock (quantity > 0).
     *
     * @return a list of products currently in stock
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0 ORDER BY p.name ASC")
    List<Product> findAllInStock();

    /**
     * Find products that are out of stock (quantity = 0).
     *
     * @return a list of products out of stock
     */
    @Query("SELECT p FROM Product p WHERE p.stock = 0 ORDER BY p.name ASC")
    List<Product> findAllOutOfStock();
}
