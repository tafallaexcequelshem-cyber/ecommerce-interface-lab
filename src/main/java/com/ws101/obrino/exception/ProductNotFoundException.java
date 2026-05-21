package com.ws101.obrino.exception;

/**
 * Exception thrown when a requested product cannot be found.
 *
 * This exception is thrown by the ProductService when a GET request
 * attempts to retrieve a product by ID that does not exist in the system.
 *
 * @author Obrino
 * @version 1.0
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Constructs a new ProductNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the product was not found
     */
    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ProductNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
