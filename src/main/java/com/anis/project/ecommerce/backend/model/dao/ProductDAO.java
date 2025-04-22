package com.anis.project.ecommerce.backend.model.dao;

import com.anis.project.ecommerce.backend.model.Product;
import org.springframework.data.repository.ListCrudRepository;

/**
 * Data Access Object for accessing Product data.
 */
public interface ProductDAO extends ListCrudRepository<Product, Long> {
}