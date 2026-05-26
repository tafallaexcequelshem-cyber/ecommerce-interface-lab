package com.ws101.obrino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User entity representing an e-commerce user with authentication support.
 *
 * This JPA entity maps to the 'users' table and implements Spring Security's UserDetails interface,
 * enabling seamless integration with Spring Security for authentication and authorization.
 * Users can have roles such as ADMIN, SELLER, or USER.
 *
 * @author Obrino
 * @version 1.0
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    /**
     * Unique identifier for the user.
     * Auto-generated using database auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for login.
     * Required field and must be unique in the database.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Hashed password for the user.
     * Never store plain-text passwords. Passwords are hashed using BCrypt.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Email address of the user.
     * Optional field for contact purposes.
     */
    @Column(length = 100)
    private String email;

    /**
     * Full name of the user.
     */
    @Column(length = 100)
    private String fullName;

    /**
     * User's role in the system.
     * Supported roles: ADMIN, SELLER, USER
     */
    @Column(nullable = false, length = 50)
    private String role;

    /**
     * Account enabled status.
     * Default is true; can be set to false to disable the account.
     */
    @Column(nullable = false)
    private Boolean accountEnabled = true;

    /**
     * Timestamp when the user was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Pre-persist lifecycle hook to set creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Pre-update lifecycle hook to update the modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Returns the authorities granted to the user.
     * Maps the user's role to a Spring Security GrantedAuthority with ROLE_ prefix.
     *
     * @return a collection of authorities granted to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the account is valid (not expired), false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user account is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if credentials are still valid (not expired), false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * Returns the value of the accountEnabled field.
     *
     * @return true if the user account is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return this.accountEnabled;
    }
}
