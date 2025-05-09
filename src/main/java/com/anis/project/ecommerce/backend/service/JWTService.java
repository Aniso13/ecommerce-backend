package com.anis.project.ecommerce.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.anis.project.ecommerce.backend.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service for handling JWTs for user authentication.
 */
@Service
public class JWTService {

  /** The secret key to encrypt the JWTs with. */
  @Value("${jwt.algorithm.key}")
  private String algorithmKey;
  /** The issuer the JWT is signed with. */
  @Value("${jwt.issuer}")
  private String issuer;
  /** How many seconds from generation should the JWT expire? */
  @Value("${jwt.expiryInSeconds}")
  private int expiryInSeconds;
  /** The algorithm generated post construction. */
  private Algorithm algorithm;
  /** The JWT claim key for the username. */
  private static final String USERNAME_KEY = "USERNAME";
  private static final String EMAIL_KEY = "EMAIL";

  /**
   * Post construction method.
   */
  @PostConstruct
  public void postConstruct() {
    algorithm = Algorithm.HMAC256(algorithmKey);
  }

  /**
   * Generates a JWT based on the given user.
   * @param user The user to generate for.
   * @return The JWT.
   */
  public String generateJWT(LocalUser user) {
    return JWT.create()
            .withClaim(USERNAME_KEY, user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
            .withIssuer(issuer)
            .sign(algorithm);
  }

  /**
   * Generates a special token for verification of an email.
   * @param user The user to create the token for.
   * @return The token generated.
   */
  public String generateVerificationJWT(LocalUser user) {
    return JWT.create()
            .withClaim(EMAIL_KEY, user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
            .withIssuer(issuer)
            .sign(algorithm);
  }

  /**
   * Gets the username out of a given JWT.
   * @param token The JWT to decode.
   * @return The username stored inside.
   */
  public String getUsername(String token) {
    return JWT.decode(token).getClaim(USERNAME_KEY).asString();
  }

}