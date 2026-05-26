package com.ws101.obrino.controller;

import com.ws101.obrino.dto.LoginUserDto;
import com.ws101.obrino.dto.RegisterUserDto;
import com.ws101.obrino.model.User;
import com.ws101.obrino.repository.UserRepository;
import com.ws101.obrino.service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication operations with JWT support.
 *
 * This controller exposes HTTP endpoints for user registration and login.
 * All endpoints use the /api/v1/auth base path and handle both success and error scenarios.
 *
 * Features:
 * - User registration with validation
 * - Password hashing with BCrypt
 * - Duplicate username prevention
 * - JWT token generation on successful login
 * - Token-based (stateless) authentication
 *
 * @author Obrino
 * @version 2.0 (JWT-based)
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Constructor for dependency injection of required services.
     *
     * @param userRepository for accessing user data from the database
     * @param passwordEncoder for hashing passwords securely
     * @param authenticationManager for handling authentication
     * @param jwtUtil for JWT token generation and validation
     */
    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
     * Log in a user and return a JWT token.
     *
     * HTTP Method: POST
     * Endpoint: POST /api/v1/auth/login
     * Request Body: LoginUserDto with validation
     * Response Status: 200 OK (success with JWT token), 400 Bad Request (validation error), 401 Unauthorized (authentication failed)
     *
     * Process:
     * 1. Validate input using @Valid annotation
     * 2. Authenticate user with provided credentials using AuthenticationManager
     * 3. Generate JWT token for authenticated user
     * 4. Return token in response body for client storage
     * 5. Client includes token in Authorization header for subsequent requests
     *
     * @param loginDto the login credentials (validated)
     * @return ResponseEntity with JWT token or error details
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginUserDto loginDto) {
        try {
            // 1. Attempt to authenticate using the provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            // 2. Get authenticated user's details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 3. Generate JWT token for the authenticated user
            String token = jwtUtil.generateToken(userDetails);

            // 4. Build success response with JWT token
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400); // 24 hours in seconds

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
     * Logout endpoint (client-side token removal).
     *
     * HTTP Method: POST
     * Endpoint: POST /api/v1/auth/logout
     * Response Status: 200 OK
     *
     * Note: Since JWT authentication is stateless, logout is handled client-side
     * by removing the token. This endpoint is provided for consistency and
     * can be extended to maintain a token blacklist if needed.
     *
     * @return ResponseEntity with logout confirmation
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Logout successful. Please remove the token from client storage.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
