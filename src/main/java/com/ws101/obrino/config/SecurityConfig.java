package com.ws101.obrino.config;

import com.ws101.obrino.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Security configuration for the e-commerce application with JWT authentication.
 *
 * This configuration class sets up Spring Security with JWT token-based authentication:
 * - Stateless session management (no HTTP sessions)
 * - JWT token extraction and validation via JwtAuthenticationFilter
 * - Public endpoints for login and registration
 * - Protected endpoints requiring valid JWT tokens
 * - CORS configuration for cross-origin requests
 * - Exception handling for unauthorized access
 *
 * @author Obrino
 * @version 2.0 (JWT-based)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor for dependency injection of required services and filters.
     *
     * @param customUserDetailsService the service for loading user details
     * @param jwtAuthenticationFilter the JWT authentication filter
     */
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the security filter chain for JWT-based stateless authentication.
     *
     * Configuration details:
     * - Stateless sessions: No HTTP sessions, JWT tokens are used for authentication
     * - Public endpoints:
     *   - GET /api/v1/products (product listing)
     *   - POST /api/v1/auth/register (user registration)
     *   - POST /api/v1/auth/login (user login)
     * - Protected endpoints:
     *   - POST /api/v1/orders (requires USER or ADMIN role)
     *   - DELETE /api/v1/products/{id} (requires ADMIN role)
     *   - PUT /api/v1/products/{id} (requires ADMIN role)
     *   - POST /api/v1/products (requires ADMIN role)
     * - Static resources: Always accessible
     *
     * JWT Filter Chain:
     * - JwtAuthenticationFilter runs before UsernamePasswordAuthenticationFilter
     * - Extracts token from Authorization header (Bearer scheme)
     * - Validates token and sets authentication context
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS configuration
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList(
                            "http://localhost:3000",
                            "http://localhost:8080",
                            "http://localhost:5500",
                            "http://127.0.0.1:5500"
                    ));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))
                
                // Disable CSRF as we are using stateless JWT authentication
                .csrf(csrf -> csrf.disable())
                
                // Configure session management to be stateless (no HTTP sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/v1/products").permitAll()
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/login", "/signup").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // User-specific endpoints - requires authentication
                        .requestMatchers("POST", "/api/v1/orders").hasAnyRole("USER", "ADMIN")
                        
                        // Admin-only endpoints - requires ADMIN role
                        .requestMatchers("DELETE", "/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers("POST", "/api/v1/products").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/v1/products/**").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // Add our custom JWT filter before the standard UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                // Exception handling for unauthorized access
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(401);
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" 
                                    + authException.getMessage() + "\"}");
                        })
                );

        return http.build();
    }

    /**
     * Configures the DAO authentication provider.
     *
     * This provider handles username/password authentication by:
     * 1. Loading the user from the database via CustomUserDetailsService
     * 2. Encoding the provided password with the password encoder
     * 3. Comparing the encoded password with the stored password
     *
     * @return the configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Creates a password encoder for hashing and verifying passwords.
     *
     * BCryptPasswordEncoder is used because it:
     * - Is one of the most secure password hashing algorithms
     * - Automatically generates a salt for each password
     * - Increases computational cost over time to prevent brute-force attacks
     * - Is the recommended choice by Spring Security
     *
     * @return a BCrypt-based password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager as a bean.
     *
     * This allows other parts of the application to programmatically perform authentication.
     * Useful for custom authentication endpoints that need to manually trigger authentication.
     *
     * @param config the AuthenticationConfiguration used to build the AuthenticationManager
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
