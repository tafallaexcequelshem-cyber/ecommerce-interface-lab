package com.ws101.obrino.controller;

import com.ws101.obrino.model.Product;
import com.ws101.obrino.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * REST Controller for managing product operations.
 *
 * This controller exposes HTTP endpoints for CRUD operations on products
 * and advanced filtering. All endpoints use the /api/v1/products base path
 * and return appropriate HTTP status codes.
 *
 * @author Obrino
 * @version 1.0
 * @see ProductService
 */
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for dependency injection of ProductService.
     *
     * @param productService the service layer for product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     *
     * HTTP Method: GET
     * Endpoint: GET /api/v1/products
     * Response Status: 200 OK
     *
     * @return a {@code ResponseEntity} containing a list of all products
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<Map<String, Object>> response = new ArrayList<>();
        for (Product product : products) {
            response.add(Map.of(
                "id", product.getId(),
                "name", product.getName(),
                "description", product.getDescription() != null ? product.getDescription() : "",
                "price", product.getPrice().toString(),
                "stock", product.getStock(),
                "imageUrl", product.getImageUrl() != null ? product.getImageUrl() : "",
                "createdAt", product.getCreatedAt(),
                "updatedAt", product.getUpdatedAt()
            ));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a single product by ID.
     *
     * HTTP Method: GET
     * Endpoint: GET /api/v1/products/{id}
     * Response Status: 200 OK on success, 404 Not Found if product doesn't exist
     *
     * @param id the product ID to retrieve (must be a valid positive number)
     * @return a {@code ResponseEntity} containing the requested product
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Filters products based on specified criteria.
     *
     * HTTP Method: GET
     * Endpoint: GET /api/v1/products/filter?filterType=<type>&filterValue=<value>
     * Response Status: 200 OK, 400 Bad Request for invalid filters
     *
     * Supported filter types:
     * - "category": Filter by product category
     * - "name": Filter by product name (substring match)
     * - "price_min": Filter by minimum price
     * - "price_max": Filter by maximum price
     * - "price_range": Filter by price range (format: "min,max")
     *
     * Example requests:
     * - GET /api/v1/products/filter?filterType=category&filterValue=Oversized
     * - GET /api/v1/products/filter?filterType=price_range&filterValue=300,600
     * - GET /api/v1/products/filter?filterType=name&filterValue=tee
     *
     * @param filterType the type of filter to apply (category, name, price_min, price_max, price_range)
     * @param filterValue the value for the filter
     * @return a {@code ResponseEntity} containing a list of filtered products
     * @throws IllegalArgumentException if filter type or value is invalid
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam String filterType,
            @RequestParam String filterValue) {
        List<Product> filteredProducts = productService.filterProducts(filterType, filterValue);
        return ResponseEntity.ok(filteredProducts);
    }

    /**
     * Creates a new product.
     *
     * HTTP Method: POST
     * Endpoint: POST /api/v1/products
     * Request Body: JSON representation of the product
     * Response Status: 201 Created on success, 400 Bad Request for invalid data
     *
     * Required fields in request body:
     * - name: String (minimum 3 characters)
     * - price: Double (must be positive)
     * - category: String
     * - stock: Integer (must be non-negative)
     *
     * Optional fields:
     * - description: String
     * - imageUrl: String
     *
     * Example request body:
     * {
     *   "name": "New Product",
     *   "description": "A great product",
     *   "price": 450.0,
     *   "category": "Oversized",
     *   "stock": 100,
     *   "imageUrl": "product.jpg"
     * }
     *
     * @param product the product data to create
     * @return a {@code ResponseEntity} containing the created product with 201 status
     * @throws IllegalArgumentException if product validation fails
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Updates an entire product (full replacement).
     *
     * HTTP Method: PUT
     * Endpoint: PUT /api/v1/products/{id}
     * Request Body: Complete JSON representation of the product
     * Response Status: 200 OK on success, 404 Not Found if product doesn't exist, 400 Bad Request for invalid data
     *
     * All fields except ID are replaced with values from the request body.
     * This is a full replacement operation (all fields must be provided).
     *
     * @param id the ID of the product to update
     * @param product the new product data (all fields required)
     * @return a {@code ResponseEntity} containing the updated product
     * @throws ProductNotFoundException if no product with the given ID exists
     * @throws IllegalArgumentException if product validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Partially updates a product.
     *
     * HTTP Method: PATCH
     * Endpoint: PATCH /api/v1/products/{id}
     * Request Body: JSON with only the fields to update
     * Response Status: 200 OK on success, 404 Not Found if product doesn't exist
     *
     * Only the fields provided in the request body are updated.
     * Fields not included in the request are left unchanged.
     *
     * Example request body (only update price and stock):
     * {
     *   "price": 500.0,
     *   "stock": 75
     * }
     *
     * @param id the ID of the product to partially update
     * @param product the product data with only fields to update
     * @return a {@code ResponseEntity} containing the updated product
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        Product updatedProduct = productService.partialUpdateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Deletes a product.
     *
     * HTTP Method: DELETE
     * Endpoint: DELETE /api/v1/products/{id}
     * Response Status: 204 No Content on success, 404 Not Found if product doesn't exist
     *
     * Removes the product with the specified ID from the system.
     *
     * @param id the ID of the product to delete
     * @return a {@code ResponseEntity} with no content (204 status)
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
