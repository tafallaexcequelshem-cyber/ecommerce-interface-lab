package com.ws101.obrino.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login requests.
 *
 * This DTO is used to validate incoming login credentials.
 * Both username and password are required for authentication.
 *
 * @author Obrino
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {

    /**
     * Username for login.
     * Required field - must not be blank.
     */
    @NotBlank(message = "Username is required for login")
    private String username;

    /**
     * Password for login.
     * Required field - must not be blank.
     */
    @NotBlank(message = "Password is required for login")
    private String password;
}
