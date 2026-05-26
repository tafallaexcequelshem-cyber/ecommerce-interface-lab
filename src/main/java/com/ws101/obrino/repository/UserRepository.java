package com.ws101.obrino.repository;

import com.ws101.obrino.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 *
 * Provides database access methods for User entities using Spring Data JPA.
 * Automatically implements CRUD operations and custom query methods defined below.
 *
 * @author Obrino
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, otherwise empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username exists in the database.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the User if found, otherwise empty
     */
    Optional<User> findByEmail(String email);
}
