package com.anis.project.ecommerce.backend.model.dao;

import com.anis.project.ecommerce.backend.model.LocalUser;
import com.anis.project.ecommerce.backend.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * Data Access Object for the VerificationToken data.
 */
public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

  Optional<VerificationToken> findByToken(String token);

  void deleteByUser(LocalUser user);

}