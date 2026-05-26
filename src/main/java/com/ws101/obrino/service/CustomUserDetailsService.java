package com.ws101.obrino.service;

import com.ws101.obrino.model.User;
import com.ws101.obrino.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 *
 * This service is responsible for loading user details during the authentication process.
 * Spring Security calls this service's loadUserByUsername() method when a user attempts to log in.
 * The service fetches the user from the database and returns them as a UserDetails object,
 * which Spring Security then uses to validate the provided credentials.
 *
 * @author Obrino
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection of UserRepository.
     *
     * @param userRepository the repository for accessing user data from the database
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by username during authentication.
     *
     * This method is called by Spring Security's authentication mechanism when a user
     * attempts to log in. It retrieves the user from the database by username and returns
     * the User object (which implements UserDetails) for credential validation.
     *
     * @param username the username to search for
     * @return the UserDetails object (User entity) containing user information and authorities
     * @throws UsernameNotFoundException if no user with the given username exists in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
