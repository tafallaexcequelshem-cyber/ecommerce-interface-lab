package com.ws101.obrino.repository;

import com.ws101.obrino.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for Category entity.
 *
 * Extends JpaRepository to provide CRUD operations for the Category entity.
 *
 * @author Obrino
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by its name.
     *
     * @param name the category name
     * @return an Optional containing the category if found
     */
    Optional<Category> findByName(String name);
}
