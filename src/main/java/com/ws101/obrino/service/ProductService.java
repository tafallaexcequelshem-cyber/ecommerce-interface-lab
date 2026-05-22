package com.ws101.obrino.service;

import com.ws101.obrino.model.Product;
import com.ws101.obrino.model.Category;
import com.ws101.obrino.exception.ProductNotFoundException;
import com.ws101.obrino.repository.ProductRepository;
import com.ws101.obrino.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

/**
 * Service class for product-related operations.
 *
 * Provides business logic for filtering, searching, and managing products.
 * This class acts as an intermediary between the API controller and the
 * data access layer. Uses Spring Data JPA repositories for database persistence.
 *
 * @author Obrino
 * @version 2.0
 * @see Product
 * @see ProductRepository
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor for dependency injection of repositories.
     *
     * @param productRepository the product repository for database operations
     * @param categoryRepository the category repository for database operations
     */
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return a {@code List<Product>} containing all products in the system.
     * Returns an empty list if no products exist.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the product ID to search for. Must be a positive number.
     * @return the {@code Product} with the specified ID
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with ID %d not found", id)
                ));
    }

    /**
     * Creates a new product and saves it to the database.
     *
     * @param product the product to be created. Must have name, price, and category.
     * @return the created {@code Product} with an auto-generated ID
     * @throws IllegalArgumentException if product name is empty, price is negative,
     *         or category is empty
     */
    public Product createProduct(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    /**
     * Updates an existing product completely (all fields).
     *
     * @param id the ID of the product to update
     * @param updatedProduct the new product data. Must have name, price, and category.
     * @return the updated {@code Product}
     * @throws ProductNotFoundException if the product with given ID does not exist
     * @throws IllegalArgumentException if product data is invalid
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);
        validateProduct(updatedProduct);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        return productRepository.save(existingProduct);
    }

    /**
     * Partially updates a product (only specified fields).
     *
     * @param id the ID of the product to update
     * @param partialProduct the product object containing only fields to update
     * @return the updated {@code Product}
     * @throws ProductNotFoundException if the product with given ID does not exist
     */
    public Product partialUpdateProduct(Long id, Product partialProduct) {
        Product existingProduct = getProductById(id);

        if (partialProduct.getName() != null && !partialProduct.getName().isEmpty()) {
            existingProduct.setName(partialProduct.getName());
        }
        if (partialProduct.getDescription() != null) {
            existingProduct.setDescription(partialProduct.getDescription());
        }
        if (partialProduct.getPrice() != null && partialProduct.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            existingProduct.setPrice(partialProduct.getPrice());
        }
        if (partialProduct.getCategory() != null) {
            existingProduct.setCategory(partialProduct.getCategory());
        }
        if (partialProduct.getStock() != null && partialProduct.getStock() >= 0) {
            existingProduct.setStock(partialProduct.getStock());
        }
        if (partialProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(partialProduct.getImageUrl());
        }

        return productRepository.save(existingProduct);
    }

    /**
     * Deletes a product from the database.
     *
     * @param id the ID of the product to delete
     * @throws ProductNotFoundException if the product with given ID does not exist
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Filters products based on specified criteria.
     *
     * Supported filter types:
     * - "category": Filter by product category (exact match)
     * - "name": Filter by product name (case-insensitive substring match)
     * - "price_min": Filter by minimum price
     * - "price_max": Filter by maximum price
     * - "price_range": Filter by price range (format: "min,max")
     *
     * @param filterType the type of filter to apply. Must not be null or empty.
     * @param filterValue the value for the filter. Must not be null or empty.
     * @return a {@code List<Product>} matching the filter criteria.
     * Returns an empty list if no products match.
     * @throws IllegalArgumentException if filterType is invalid or filterValue is invalid
     *
     * @see #filterProductWithCategory(String)
     * @see #filterProductWithPrice(Double, Double)
     */
    public List<Product> filterProducts(String filterType, String filterValue) {
        if (filterType == null || filterType.isEmpty()) {
            throw new IllegalArgumentException("Filter type cannot be null or empty");
        }
        if (filterValue == null || filterValue.isEmpty()) {
            throw new IllegalArgumentException("Filter value cannot be null or empty");
        }

        return switch (filterType.toLowerCase()) {
            case "category" -> filterProductWithCategory(filterValue);
            case "name" -> filterProductWithName(filterValue);
            case "price_min" -> filterProductWithMinPrice(new BigDecimal(filterValue));
            case "price_max" -> filterProductWithMaxPrice(new BigDecimal(filterValue));
            case "price_range" -> filterProductWithPriceRange(filterValue);
            default -> throw new IllegalArgumentException(
                    String.format("Invalid filter type: %s. Supported types: category, name, price_min, price_max, price_range", filterType)
            );
        };
    }

    /**
     * Filters products by category.
     *
     * @param categoryName the category name to filter by
     * @return a {@code List<Product>} containing all products in the specified category
     */
    private List<Product> filterProductWithCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    /**
     * Filters products by name.
     *
     * @param name the name substring to search for (case-insensitive)
     * @return a {@code List<Product>} containing products whose names contain the search term
     */
    private List<Product> filterProductWithName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Filters products by minimum price.
     *
     * @param minPrice the minimum price threshold (inclusive)
     * @return a {@code List<Product>} containing all products with price >= minPrice
     * @throws IllegalArgumentException if minPrice is negative
     */
    private List<Product> filterProductWithMinPrice(BigDecimal minPrice) {
        if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        return productRepository.findByPriceRange(minPrice, new BigDecimal("999999999"));
    }

    /**
     * Filters products by maximum price.
     *
     * @param maxPrice the maximum price threshold (inclusive)
     * @return a {@code List<Product>} containing all products with price <= maxPrice
     * @throws IllegalArgumentException if maxPrice is negative
     */
    private List<Product> filterProductWithMaxPrice(BigDecimal maxPrice) {
        if (maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        return productRepository.findByPriceRange(BigDecimal.ZERO, maxPrice);
    }

    /**
     * Filters products by price range.
     *
     * @param priceRange a string in the format "minPrice,maxPrice" (e.g., "100,500")
     * @return a {@code List<Product>} containing products with price within the range (inclusive)
     * @throws IllegalArgumentException if the format is invalid or prices are invalid
     *
     * @see #filterProductWithPrice(BigDecimal, BigDecimal)
     */
    private List<Product> filterProductWithPriceRange(String priceRange) {
        try {
            String[] prices = priceRange.split(",");
            if (prices.length != 2) {
                throw new IllegalArgumentException("Price range must be in format 'minPrice,maxPrice'");
            }
            BigDecimal minPrice = new BigDecimal(prices[0].trim());
            BigDecimal maxPrice = new BigDecimal(prices[1].trim());
            return filterProductWithPrice(minPrice, maxPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price values must be valid numbers", e);
        }
    }

    /**
     * Filters products by price range.
     *
     * Retrieves all products where the price falls within the specified range,
     * inclusive of both boundaries.
     *
     * @param minPrice the minimum price threshold (inclusive).
     * Must be non-negative and less than or equal to maxPrice.
     * @param maxPrice the maximum price threshold (inclusive).
     * Must be non-negative and greater than or equal to minPrice.
     * @return a {@code List<Product>} containing all products with price within
     * [minPrice, maxPrice]. Returns an empty list if no products match the criteria.
     * @throws IllegalArgumentException if minPrice is negative, maxPrice is
     * negative, or minPrice > maxPrice
     */
    private List<Product> filterProductWithPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Validates product data before creation or update.
     *
     * Checks that:
     * - Product name is not null/empty and has minimum length
     * - Price is a positive number
     * - Category is not null
     * - Stock quantity is non-negative
     *
     * @param product the product to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getName().length() < 3) {
            throw new IllegalArgumentException("Product name must be at least 3 characters long");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be a positive number");
        }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }
}
