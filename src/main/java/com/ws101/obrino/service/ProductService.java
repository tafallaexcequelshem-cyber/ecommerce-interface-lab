package com.ws101.obrino.service;

import com.ws101.obrino.model.Product;
import com.ws101.obrino.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for product-related operations.
 *
 * Provides business logic for filtering, searching, and managing products.
 * This class acts as an intermediary between the API controller and the
 * data access layer. Currently uses in-memory storage with a List.
 *
 * @author Obrino
 * @version 1.0
 * @see Product
 */
@Service
public class ProductService {

    private final List<Product> productList = new ArrayList<>();
    private Long nextId = 1L;

    /**
     * Constructor that initializes the product list with sample data.
     */
    public ProductService() {
        initializeProducts();
    }

    /**
     * Initializes the product list with 12 sample products.
     * This method populates the in-memory storage with test data.
     */
    private void initializeProducts() {
        productList.add(new Product(nextId++, "One Piece - Oversized Tee", "Comfortable oversized fit perfect for casual wear", 600.0, "Oversized", 50, "product1.jpg"));
        productList.add(new Product(nextId++, "Eternal - Oversized Tee", "Timeless design with premium quality fabric", 300.0, "Oversized", 45, "product2.jpg"));
        productList.add(new Product(nextId++, "Upseen - Standard Tee", "Classic standard fit for everyday wear", 400.0, "Standard Round Neck", 60, "product3.jpg"));
        productList.add(new Product(nextId++, "Urban Vibes - Oversized Tee", "Street style inspired oversized tee", 550.0, "Oversized", 35, "product4.jpg"));
        productList.add(new Product(nextId++, "Minimalist White - Standard Tee", "Pure white classic fit tee", 350.0, "Standard Round Neck", 70, "product5.jpg"));
        productList.add(new Product(nextId++, "Retro Black - Oversized Tee", "Vintage inspired oversized design", 500.0, "Oversized", 40, "product6.jpg"));
        productList.add(new Product(nextId++, "Premium Gray - Standard Tee", "Premium quality gray standard fit", 450.0, "Standard Round Neck", 55, "product7.jpg"));
        productList.add(new Product(nextId++, "Bold Navy - Oversized Tee", "Navy blue oversized comfort fit", 580.0, "Oversized", 30, "product8.jpg"));
        productList.add(new Product(nextId++, "Summer Cool - Standard Tee", "Lightweight perfect for summer", 380.0, "Standard Round Neck", 65, "product9.jpg"));
        productList.add(new Product(nextId++, "Classic Red - Oversized Tee", "Bold red statement piece", 520.0, "Oversized", 25, "product10.jpg"));
        productList.add(new Product(nextId++, "Earth Tone - Standard Tee", "Natural earth tone colors", 420.0, "Standard Round Neck", 50, "product11.jpg"));
        productList.add(new Product(nextId++, "Premium Charcoal - Oversized Tee", "Premium charcoal oversized fit", 600.0, "Oversized", 20, "product12.jpg"));
    }

    /**
     * Retrieves all products from the in-memory storage.
     *
     * @return a {@code List<Product>} containing all products in the system.
     * Returns an empty list if no products exist.
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the product ID to search for. Must be a positive number.
     * @return the {@code Product} with the specified ID
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    public Product getProductById(Long id) {
        return productList.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with ID %d not found", id)
                ));
    }

    /**
     * Creates a new product and adds it to the in-memory storage.
     *
     * @param product the product to be created. Must have name, price, and category.
     * @return the created {@code Product} with an auto-generated ID
     * @throws IllegalArgumentException if product name is empty, price is negative,
     *         or category is empty
     */
    public Product createProduct(Product product) {
        validateProduct(product);
        product.setId(nextId++);
        productList.add(product);
        return product;
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

        return existingProduct;
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
        if (partialProduct.getPrice() != null && partialProduct.getPrice() > 0) {
            existingProduct.setPrice(partialProduct.getPrice());
        }
        if (partialProduct.getCategory() != null && !partialProduct.getCategory().isEmpty()) {
            existingProduct.setCategory(partialProduct.getCategory());
        }
        if (partialProduct.getStock() != null && partialProduct.getStock() >= 0) {
            existingProduct.setStock(partialProduct.getStock());
        }
        if (partialProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(partialProduct.getImageUrl());
        }

        return existingProduct;
    }

    /**
     * Deletes a product from the in-memory storage.
     *
     * @param id the ID of the product to delete
     * @throws ProductNotFoundException if the product with given ID does not exist
     */
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productList.remove(product);
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
            case "price_min" -> filterProductWithMinPrice(Double.parseDouble(filterValue));
            case "price_max" -> filterProductWithMaxPrice(Double.parseDouble(filterValue));
            case "price_range" -> filterProductWithPriceRange(filterValue);
            default -> throw new IllegalArgumentException(
                    String.format("Invalid filter type: %s. Supported types: category, name, price_min, price_max, price_range", filterType)
            );
        };
    }

    /**
     * Filters products by category.
     *
     * @param category the category to filter by (exact match, case-sensitive)
     * @return a {@code List<Product>} containing all products in the specified category
     */
    private List<Product> filterProductWithCategory(String category) {
        return productList.stream()
                .filter(product -> product.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * Filters products by name.
     *
     * @param name the name substring to search for (case-insensitive)
     * @return a {@code List<Product>} containing products whose names contain the search term
     */
    private List<Product> filterProductWithName(String name) {
        return productList.stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filters products by minimum price.
     *
     * @param minPrice the minimum price threshold (inclusive)
     * @return a {@code List<Product>} containing all products with price >= minPrice
     * @throws IllegalArgumentException if minPrice is negative
     */
    private List<Product> filterProductWithMinPrice(Double minPrice) {
        if (minPrice < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        return productList.stream()
                .filter(product -> product.getPrice() >= minPrice)
                .collect(Collectors.toList());
    }

    /**
     * Filters products by maximum price.
     *
     * @param maxPrice the maximum price threshold (inclusive)
     * @return a {@code List<Product>} containing all products with price <= maxPrice
     * @throws IllegalArgumentException if maxPrice is negative
     */
    private List<Product> filterProductWithMaxPrice(Double maxPrice) {
        if (maxPrice < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        return productList.stream()
                .filter(product -> product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * Filters products by price range.
     *
     * @param priceRange a string in the format "minPrice,maxPrice" (e.g., "100,500")
     * @return a {@code List<Product>} containing products with price within the range (inclusive)
     * @throws IllegalArgumentException if the format is invalid or prices are invalid
     *
     * @see #filterProductWithPrice(Double, Double)
     */
    private List<Product> filterProductWithPriceRange(String priceRange) {
        try {
            String[] prices = priceRange.split(",");
            if (prices.length != 2) {
                throw new IllegalArgumentException("Price range must be in format 'minPrice,maxPrice'");
            }
            Double minPrice = Double.parseDouble(prices[0].trim());
            Double maxPrice = Double.parseDouble(prices[1].trim());
            return filterProductWithPrice(minPrice, maxPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price values must be valid numbers", e);
        }
    }

    /**
     * Filters products by price range.
     *
     * Retrieves all products where the price falls within the specified range,
     * inclusive of both boundaries. Products are returned in the order they
     * appear in the underlying data source.
     *
     * @param minPrice the minimum price threshold (inclusive).
     * Must be non-negative and less than or equal to maxPrice.
     * @param maxPrice the maximum price threshold (inclusive).
     * Must be non-negative and greater than or equal to minPrice.
     * @return a {@code List<Product>} containing all products with price within
     * [minPrice, maxPrice]. Returns an empty list if no products match the
     * criteria or if the data source is empty.
     * @throws IllegalArgumentException if minPrice is negative, maxPrice is
     * negative, or minPrice > maxPrice
     */
    private List<Product> filterProductWithPrice(Double minPrice, Double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        return productList.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * Validates product data before creation or update.
     *
     * Checks that:
     * - Product name is not null/empty and has minimum length
     * - Price is a positive number
     * - Category is not null/empty
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
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be a positive number");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }
}
