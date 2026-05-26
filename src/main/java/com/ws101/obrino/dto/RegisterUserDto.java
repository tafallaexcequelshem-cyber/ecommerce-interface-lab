package com.ws101.obrino.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 *
 * This DTO is used to validate incoming registration data before creating a new user.
 * Spring's Bean Validation framework uses these annotations to ensure data integrity.
 *
 * @author Obrino
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    /**
     * Unique username for the new user.
     * Must not be blank and should be between 4 and 20 characters.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    /**
     * Password for the new user.
     * Must not be blank and should be at least 8 characters for security.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    /**
     * Password confirmation field.
     * Must match the password field (validation handled in service layer).
     */
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    /**
     * Email address of the user.
     * Must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Full name of the user.
     * Optional field but if provided, should be between 2 and 100 characters.
     */
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /**
     * Role for the new user.
     * Allowed values: USER, ADMIN, SELLER
     * Defaults to USER if not specified.
     */
    @Pattern(regexp = "USER|ADMIN|SELLER", message = "Role must be USER, ADMIN, or SELLER")
    private String role;
}
