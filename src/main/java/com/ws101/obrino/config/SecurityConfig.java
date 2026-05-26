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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Security configuration for the e-commerce application.
 *
 * This configuration class sets up Spring Security for the application, including:
 * - HTTP security rules and endpoint protection
 * - Authentication providers and user details loading
 * - Password encoding strategy using BCrypt
 * - CSRF protection for form submissions
 * - Session management
 * - CORS configuration
 *
 * @author Obrino
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Constructor for dependency injection of CustomUserDetailsService.
     *
     * @param customUserDetailsService the service for loading user details from the database
     */
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Configures the security filter chain for incoming HTTP requests.
     *
     * Access rules:
     * - GET /api/v1/products: Public access (product listing)
     * - POST /api/v1/auth/register: Public access (user registration)
     * - GET /login: Public access (login page)
     * - POST /api/v1/orders: Requires USER or ADMIN role
     * - DELETE /api/v1/products/{id}: Requires ADMIN role
     * - All other requests: Require authentication
     *
     * Features:
     * - Form-based login with default /login and /logout endpoints
     * - Session-based authentication with JSESSIONID cookies
     * - CSRF protection enabled for session security
     * - Automatic redirect to login page for unauthorized requests
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
                    config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))
                // Authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/v1/products").permitAll()
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        // User-specific endpoints - requires authentication
                        .requestMatchers("POST", "/api/v1/orders").hasAnyRole("USER", "ADMIN")
                        // Admin-only endpoints - requires ADMIN role
                        .requestMatchers("DELETE", "/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers("POST", "/api/v1/products").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/v1/products/**").hasRole("ADMIN")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Form login configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/api/v1/products")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                // CSRF protection enabled for session security
                .csrf(csrf -> csrf.disable())
                // Session management
                .sessionManagement(session -> session
                        .sessionFixationProtection(org.springframework.security.config.http.SessionFixationProtection.MIGRATE_SESSION)
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                                .expiredUrl("/login")
                        )
                );

        return http.build();
    }

    /**
     * Configures the DAO authentication provider.
     *
     * This provider handles username/password authentication by:
     * 1. Loading the user from the database via CustomUserDetailsService
     * 2. Encoding the provided password with the password encoder
     * 3. Comparing the encoded password with the password stored in the database
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
     * Never store plain-text passwords. Always hash them using this encoder
     * before persisting to the database.
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
