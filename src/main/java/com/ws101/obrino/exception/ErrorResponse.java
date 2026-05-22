package com.ws101.obrino.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Standard error response format for API exceptions.
 *
 * This class is used by the GlobalExceptionHandler to return consistent
 * error responses to clients with timestamp, status code, message, and details.
 *
 * @author Obrino
 * @version 2.0
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The timestamp when the error occurred.
     * Formatted as ISO 8601 date-time.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code of the error response.
     */
    private int status;

    /**
     * Brief error message describing the problem.
     */
    private String message;

    /**
     * Detailed information about the error.
     */
    private String details;
}
