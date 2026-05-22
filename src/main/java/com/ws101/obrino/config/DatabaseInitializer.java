package com.ws101.obrino.config;

import com.ws101.obrino.model.Product;
import com.ws101.obrino.model.Category;
import com.ws101.obrino.repository.ProductRepository;
import com.ws101.obrino.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Database initialization component that loads sample data on application startup.
 *
 * This class implements CommandLineRunner to ensure sample categories and products
 * are inserted into the database when the Spring Boot application starts, but only
 * if the database is empty (count == 0).
 *
 * @author Obrino
 * @version 1.0
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor for dependency injection of repositories.
     *
     * @param productRepository the product repository
     * @param categoryRepository the category repository
     */
    public DatabaseInitializer(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Initializes the database with sample data when the application starts.
     *
     * Creates two categories (Oversized and Standard Round Neck) and 12 products.
     * Only runs if the database is empty (no existing products).
     *
     * @param args command line arguments (unused)
     * @throws Exception if database operations fail
     */
    @Override
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

            System.out.println("✓ Database initialized with 2 categories and 12 sample products");
        } else {
            System.out.println("✓ Database already populated with " + productRepository.count() + " products");
        }
    }
}
