package com.ws101.obrino.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;

/**
 * Global exception handler for all REST API endpoints.
 *
 * This class provides centralized exception handling for all REST controllers,
 * intercepting exceptions and returning consistent error response formats
 * with appropriate HTTP status codes.
 *
 * @author Obrino
 * @version 2.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ProductNotFoundException (404 Not Found).
     *
     * @param ex the ProductNotFoundException that was thrown
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and 404 status
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Product Not Found",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle EntityNotFoundException (404 Not Found).
     *
     * Catches JPA EntityNotFoundException for any entity type.
     *
     * @param ex the EntityNotFoundException that was thrown
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and 404 status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle IllegalArgumentException (400 Bad Request).
     *
     * Catches validation errors thrown during business logic.
     *
     * @param ex the IllegalArgumentException that was thrown
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MethodArgumentTypeMismatchException (400 Bad Request).
     *
     * Catches invalid type conversions in URL path variables or parameters.
     *
     * @param ex the MethodArgumentTypeMismatchException that was thrown
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        String details = String.format(
                "Parameter '%s' should be of type %s, but received '%s'",
                ex.getName(),
                ex.getRequiredType().getSimpleName(),
                ex.getValue()
        );

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter Type",
                details
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle DataIntegrityViolationException (400 Bad Request).
     *
     * Catches database constraint violations (unique constraints, foreign key violations, etc.).
     *
     * @param ex the DataIntegrityViolationException that was thrown
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and 400 status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        String details = "Database constraint violation. ";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Duplicate") || ex.getMessage().contains("unique")) {
                details += "This record already exists. Please check your input for duplicates.";
            } else if (ex.getMessage().contains("foreign key")) {
                details += "Invalid reference to a related entity.";
            } else {
                details += ex.getMostSpecificCause().getMessage();
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Data Integrity Error",
                details
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle all other generic exceptions (500 Internal Server Error).
     *
     * This is a catch-all handler for any unexpected errors.
     *
     * @param ex the Exception that was thrown
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
