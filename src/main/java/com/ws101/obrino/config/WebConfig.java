package com.ws101.obrino.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for Spring MVC.
 *
 * This configuration handles CORS (Cross-Origin Resource Sharing) setup to allow
 * the frontend application to communicate with the backend API.
 *
 * @author Obrino
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS settings for the application.
     *
     * Allows:
     * - Origins: localhost:5500 (live server), localhost:3000 (development)
     * - Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
     * - Headers: Authorization, Content-Type, Accept
     * - Credentials: true (for cookies and authentication headers)
     * - Max Age: 3600 seconds (1 hour)
     *
     * @param registry the CORS registry to add mappings to
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5500",
                        "http://localhost:3000",
                        "http://localhost:8000",
                        "http://127.0.0.1:5500"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "X-Requested-With")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
