package com.ws101.obrino.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility Service for handling JWT token operations.
 *
 * This service is responsible for:
 * - Generating JWT tokens for authenticated users
 * - Extracting claims from JWT tokens
 * - Validating JWT token signatures and expiration
 * - Checking token validity against user details
 *
 * @author Obrino
 * @version 1.0
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; // in milliseconds, e.g., 86400000 for 24 hours

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the token.
     *
     * @param token the JWT token
     * @param claimsResolver a function to extract the desired claim
     * @param <T> the type of the claim
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates the token against the user details.
     * Checks if the token is expired and if the username matches.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to compare against
     * @return true if token is valid for this user, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Generates a new JWT token for the given user details.
     *
     * @param userDetails the user details to create token for
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a token with additional claims.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details to create token for
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if the token has expired.
     *
     * @param token the JWT token
     * @return true if token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses all claims from the token.
     * Throws exception if signature is invalid or token is malformed.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Derives the SecretKey from the configured secret string.
     * Ensures the key is at least 256 bits for HS256.
     *
     * @return the SecretKey for signing and verifying tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Ensure the key is valid for HS256 (at least 256 bits / 32 bytes)
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 characters long for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
