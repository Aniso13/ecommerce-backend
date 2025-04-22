package com.anis.project.ecommerce.backend.model.dao;

import com.anis.project.ecommerce.backend.model.LocalUser;
import com.anis.project.ecommerce.backend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Data Access Object to access WebOrder data.
 */
public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

  List<WebOrder> findByUser(LocalUser user);

}