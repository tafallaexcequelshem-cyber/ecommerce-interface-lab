package com.ws101.obrino.config;

import com.ws101.obrino.model.Product;
import com.ws101.obrino.model.Category;
import com.ws101.obrino.model.User;
import com.ws101.obrino.repository.ProductRepository;
import com.ws101.obrino.repository.CategoryRepository;
import com.ws101.obrino.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Database initialization component that loads sample data on application startup.
 *
 * This class implements CommandLineRunner to ensure sample categories, products, and test users
 * are inserted into the database when the Spring Boot application starts, but only
 * if the database is empty (count == 0).
 *
 * @author Obrino
 * @version 2.0
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for dependency injection of repositories and password encoder.
     *
     * @param productRepository the product repository
     * @param categoryRepository the category repository
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder for hashing passwords
     */
    public DatabaseInitializer(ProductRepository productRepository, CategoryRepository categoryRepository,
                                UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initializes the database with sample data when the application starts.
     *
     * Creates:
     * - Two categories (Oversized and Standard Round Neck)
     * - 12 products
     * - 2 test users for testing login functionality
     *
     * Only runs if the database is empty (no existing products).
     *
     * @param args command line arguments (unused)
     * @throws Exception if database operations fail
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (productRepository.count() == 0) {
            // Create categories
            Category oversized = new Category();
            oversized.setName("Oversized");
            oversized.setDescription("Oversized fit clothing");
            
            Category standard = new Category();
            standard.setName("Standard Round Neck");
            standard.setDescription("Standard round neck clothing");
            
            categoryRepository.save(oversized);
            categoryRepository.save(standard);

            // Create sample products
            productRepository.save(new Product(null, "One Piece - Oversized Tee", "Comfortable oversized fit perfect for casual wear", new BigDecimal("600.00"), oversized, 50, "product1.jpg", null, null));
            productRepository.save(new Product(null, "Eternal - Oversized Tee", "Timeless design with premium quality fabric", new BigDecimal("300.00"), oversized, 45, "product2.jpg", null, null));
            productRepository.save(new Product(null, "Upseen - Oversized Tee", "Unique and trendy oversized collection", new BigDecimal("550.00"), oversized, 40, "product3.jpg", null, null));
            productRepository.save(new Product(null, "Paradise - Oversized Tee", "Escape to comfort with our paradise collection", new BigDecimal("400.00"), oversized, 35, "product4.jpg", null, null));
            productRepository.save(new Product(null, "Abstract - Oversized Tee", "Modern art meets fashion in abstract designs", new BigDecimal("650.00"), oversized, 30, "product5.jpg", null, null));
            productRepository.save(new Product(null, "Minimalist - Oversized Tee", "Less is more: elegant minimalist style", new BigDecimal("350.00"), oversized, 55, "product6.jpg", null, null));
            productRepository.save(new Product(null, "One Piece - Round Neck Tee", "Classic round neck from One Piece collection", new BigDecimal("500.00"), standard, 60, "product7.jpg", null, null));
            productRepository.save(new Product(null, "Eternal - Round Neck Tee", "Timeless comfort in standard round neck", new BigDecimal("250.00"), standard, 50, "product8.jpg", null, null));
            productRepository.save(new Product(null, "Urban - Round Neck Tee", "Contemporary urban design in round neck", new BigDecimal("450.00"), standard, 45, "product9.jpg", null, null));
            productRepository.save(new Product(null, "Classic - Round Neck Tee", "The classic choice that never goes out of style", new BigDecimal("300.00"), standard, 70, "product10.jpg", null, null));
            productRepository.save(new Product(null, "Premium - Round Neck Tee", "Premium quality round neck for the discerning customer", new BigDecimal("700.00"), standard, 25, "product11.jpg", null, null));
            productRepository.save(new Product(null, "Essential - Round Neck Tee", "Essential wardrobe staple in round neck", new BigDecimal("200.00"), standard, 100, "product12.jpg", null, null));

            // Create test users
            User testUser1 = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("testpass123"))
                    .email("testuser@example.com")
                    .fullName("Test User")
                    .role("USER")
                    .accountEnabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User testUser2 = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .fullName("Admin User")
                    .role("ADMIN")
                    .accountEnabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(testUser1);
            userRepository.save(testUser2);

            System.out.println("✓ Database initialized with 2 categories, 12 sample products, and 2 test users");
            System.out.println("  Test User 1: username=testuser, password=testpass123 (USER role)");
            System.out.println("  Test User 2: username=admin, password=admin123 (ADMIN role)");
        } else {
            System.out.println("✓ Database already populated with " + productRepository.count() + " products");
        }
    }
}
