package com.ws101.obrino.config;

import com.ws101.obrino.service.CustomUserDetailsService;
import com.ws101.obrino.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for validating JWT tokens in incoming requests.
 *
 * This filter:
 * 1. Extracts JWT token from the Authorization header (Bearer scheme)
 * 2. Validates the token using JwtUtil
 * 3. Extracts user details from the token
 * 4. Sets up Spring Security authentication context if token is valid
 *
 * The filter is executed once per HTTP request and doesn't require the user
 * to be authenticated before the filter runs (it handles authentication itself).
 *
 * @author Obrino
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor for dependency injection of JWT utilities and user details service.
     *
     * @param jwtUtil the JWT utility service for token operations
     * @param userDetailsService the service for loading user details from database
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Processes each HTTP request to extract and validate JWT tokens.
     *
     * Process:
     * 1. Extract the JWT token from the Authorization header
     * 2. Verify token format (must start with "Bearer ")
     * 3. Validate token signature and expiration
     * 4. Load user details from database using username in token
     * 5. Create and set authentication in Spring Security context
     * 6. Continue the filter chain
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if an error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extract the JWT token from the Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // No valid token found, continue with next filter
        }

        // Extract token by removing "Bearer " prefix (7 characters)
        jwt = authHeader.substring(7);

        try {
            // 2. Validate the token and extract the username
            username = jwtUtil.extractUsername(jwt);

            // 3. Check if user is already authenticated in the context
            if (StringUtils.hasText(username) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. Verify the token is valid for this user
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    // Create authentication token with user details
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Set request details for audit purposes
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log error or handle invalid token gracefully
            logger.error("Error processing JWT token: " + e.getMessage());
            // Continue filter chain - let security config handle unauthorized access
        }

        // 6. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
