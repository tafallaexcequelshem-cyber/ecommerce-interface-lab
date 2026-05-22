package com.ws101.obrino.util;

import com.ws101.obrino.model.Category;
import com.ws101.obrino.model.Product;
import com.ws101.obrino.repository.CategoryRepository;
import com.ws101.obrino.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Database initializer that loads sample data on application startup.
 *
 * This component implements CommandLineRunner to execute database initialization
 * when the Spring Boot application starts. It creates sample categories and products
 * to populate the database if they don't already exist.
 *
 * @author Obrino
 * @version 1.0
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository the category repository
     * @param productRepository the product repository
     */
    @Autowired
    public DatabaseInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Runs on application startup to initialize database with sample data.
     *
     * @param args command line arguments (not used)
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (productRepository.count() == 0) {
            initializeCategories();
            initializeProducts();
            System.out.println("✓ Database initialized with sample data");
        } else {
            System.out.println("✓ Database already populated, skipping initialization");
        }
    }

    /**
     * Initializes sample categories.
     */
    private void initializeCategories() {
        Category oversized = new Category("Oversized", "Oversized fit T-shirts for comfortable casual wear");
        Category standard = new Category("Standard Round Neck", "Standard fit round neck T-shirts for everyday wear");

        categoryRepository.save(oversized);
        categoryRepository.save(standard);

        System.out.println("✓ Categories initialized: Oversized, Standard Round Neck");
    }

    /**
     * Initializes sample products.
     */
    private void initializeProducts() {
        // Get categories
        Optional<Category> oversizedOpt = categoryRepository.findByName("Oversized");
        Optional<Category> standardOpt = categoryRepository.findByName("Standard Round Neck");

        if (oversizedOpt.isEmpty() || standardOpt.isEmpty()) {
            System.out.println("✗ Categories not found, cannot initialize products");
            return;
        }

        Category oversized = oversizedOpt.get();
        Category standard = standardOpt.get();

        // Create sample products
        Product[] products = {
            new Product(null, "One Piece - Oversized Tee", "Comfortable oversized fit perfect for casual wear", 600.0, oversized, 50, "product1.jpg"),
            new Product(null, "Eternal - Oversized Tee", "Timeless design with premium quality fabric", 300.0, oversized, 45, "product2.jpg"),
            new Product(null, "Upseen - Standard Tee", "Classic standard fit for everyday wear", 400.0, standard, 60, "product3.jpg"),
            new Product(null, "Urban Vibes - Oversized Tee", "Street style inspired oversized tee", 550.0, oversized, 35, "product4.jpg"),
            new Product(null, "Minimalist White - Standard Tee", "Pure white classic fit tee", 350.0, standard, 70, "product5.jpg"),
            new Product(null, "Retro Black - Oversized Tee", "Vintage inspired oversized design", 500.0, oversized, 40, "product6.jpg"),
            new Product(null, "Premium Gray - Standard Tee", "Premium quality gray standard fit", 450.0, standard, 55, "product7.jpg"),
            new Product(null, "Bold Navy - Oversized Tee", "Navy blue oversized comfort fit", 580.0, oversized, 30, "product8.jpg"),
            new Product(null, "Summer Cool - Standard Tee", "Lightweight perfect for summer", 380.0, standard, 65, "product9.jpg"),
            new Product(null, "Classic Red - Oversized Tee", "Bold red statement piece", 520.0, oversized, 25, "product10.jpg"),
            new Product(null, "Earth Tone - Standard Tee", "Natural earth tone colors", 420.0, standard, 50, "product11.jpg"),
            new Product(null, "Premium Charcoal - Oversized Tee", "Premium charcoal oversized fit", 600.0, oversized, 20, "product12.jpg")
        };

        for (Product product : products) {
            productRepository.save(product);
        }

        System.out.println("✓ Sample products initialized: " + products.length + " products added");
    }
}
