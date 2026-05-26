package com.ws101.obrino.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Product entities.
 * Used for API requests and responses with comprehensive validation.
 * Ensures that only valid product data is processed by the service layer.
 *
 * @author Obrino
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    /**
     * Product ID - populated on responses only.
     */
    private Long id;

    /**
     * Product name - required and must be between 3 and 100 characters.
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    /**
     * Product description - optional but if provided must be reasonable length.
     */
    @Size(max = 500, message = "Product description must not exceed 500 characters")
    private String description;

    /**
     * Product price - required and must be positive.
     */
    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be positive")
    @DecimalMin(value = "0.01", message = "Product price must be at least 0.01")
    private BigDecimal price;

    /**
     * Stock quantity - required and must be non-negative.
     */
    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;

    /**
     * Image URL for the product - optional.
     */
    private String imageUrl;

    /**
     * Creation timestamp - read-only, set by server.
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp - read-only, set by server.
     */
    private LocalDateTime updatedAt;
}
