package com.ws101.obrino.controller;

import com.ws101.obrino.dto.LoginUserDto;
import com.ws101.obrino.dto.RegisterUserDto;
import com.ws101.obrino.model.User;
import com.ws101.obrino.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication operations.
 *
 * This controller exposes HTTP endpoints for user registration and login.
 * All endpoints use the /api/v1/auth base path and handle both success and error scenarios.
 *
 * Features:
 * - User registration with validation
 * - Password hashing with BCrypt
 * - Duplicate username prevention
 * - Session-based authentication
 *
 * @author Obrino
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor for dependency injection of required services.
     *
     * @param userRepository for accessing user data from the database
     * @param passwordEncoder for hashing passwords securely
     * @param authenticationManager for handling authentication
     */
    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user in the system.
     *
     * HTTP Method: POST
     * Endpoint: POST /api/v1/auth/register
     * Request Body: RegisterUserDto with validation
     * Response Status: 201 Created (success), 400 Bad Request (validation/duplicate error)
     *
     * Process:
     * 1. Validate incoming request data using @Valid annotation
     * 2. Check if username already exists
     * 3. Validate that password and confirmPassword match
     * 4. Hash the password using BCrypt
     * 5. Save the new user to the database
     * 6. Return success response with user details
     *
     * @param registerDto the registration data (validated)
     * @return ResponseEntity with success message or error details
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterUserDto registerDto) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Username Already Exists");
            errorResponse.put("message", "The username '" + registerDto.getUsername() + "' is already taken. Please choose a different one.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Validate password and confirm password match
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Password Mismatch");
            errorResponse.put("message", "Password and confirm password do not match.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Create new User entity
        User newUser = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword())) // Hash password
                .email(registerDto.getEmail())
                .fullName(registerDto.getFullName())
                .role(registerDto.getRole() != null ? registerDto.getRole() : "USER") // Default to USER role
                .accountEnabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save user to database
        User savedUser = userRepository.save(newUser);

        // Build success response
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "User registered successfully");
        response.put("user", Map.of(
                "id", savedUser.getId(),
                "username", savedUser.getUsername(),
                "email", savedUser.getEmail(),
                "fullName", savedUser.getFullName(),
                "role", savedUser.getRole(),
                "createdAt", savedUser.getCreatedAt()
        ));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Log in a user (triggers Spring Security's form login).
     *
     * HTTP Method: POST
     * Endpoint: POST /api/v1/auth/login
     * Request Body: LoginUserDto with validation
     * Response Status: 200 OK (success), 400 Bad Request (validation error), 401 Unauthorized (authentication failed)
     *
     * Note: Spring Security's form login filter will handle the actual authentication.
     * This endpoint is provided for API clients that prefer JSON-based login over form submissions.
     * The endpoint validates input and returns appropriate messages.
     *
     * @param loginDto the login credentials (validated)
     * @return ResponseEntity with success message or error details
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginUserDto loginDto) {
        try {
            // Attempt to authenticate using the provided credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            // If authentication succeeds, return success response
            // Spring Security will automatically set the JSESSIONID cookie
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Login successful. Session cookie has been set.");
            response.put("sessionManagement", "Session-based authentication enabled");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            // Authentication failed
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
            errorResponse.put("error", "Authentication Failed");
            errorResponse.put("message", "Invalid username or password");

            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Logout endpoint for completing the session.
     *
     * HTTP Method: POST
     * Endpoint: POST /api/v1/auth/logout
     * Response Status: 200 OK
     *
     * Note: Spring Security's logout filter (/logout) handles session invalidation.
     * This endpoint is a convenience endpoint for API clients.
     *
     * @return ResponseEntity with logout confirmation
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Logout successful. Session has been invalidated.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
