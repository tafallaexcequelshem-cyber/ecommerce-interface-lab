package com.ws101.obrino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the E-Commerce API.
 *
 * This Spring Boot application provides RESTful API endpoints for managing
 * a product catalog with in-memory data storage.
 *
 * @author Obrino
 * @version 1.0
 */
@SpringBootApplication
public class EcommerceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApiApplication.class, args);
	}

}
