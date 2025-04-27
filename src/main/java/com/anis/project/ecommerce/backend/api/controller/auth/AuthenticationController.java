package com.anis.project.ecommerce.backend.api.controller.auth;
import com.anis.project.ecommerce.backend.exception.EmailFailureException;
import com.anis.project.ecommerce.backend.exception.UserNotVerifiedException;
import com.anis.project.ecommerce.backend.api.model.LoginBody;
import com.anis.project.ecommerce.backend.api.model.LoginResponse;
import com.anis.project.ecommerce.backend.api.model.RegistrationBody;
import com.anis.project.ecommerce.backend.exception.UserAlreadyExistsException;
import com.anis.project.ecommerce.backend.model.LocalUser;
import com.anis.project.ecommerce.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller for handling authentication requests.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  /** The user service. */
  private UserService userService;

  /**
   * Spring injected constructor.
   * @param userService
   */
  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Post Mapping to handle registering users.
   * @param registrationBody The registration information.
   * @return Response to front end.
   */
  @PostMapping("/register")
  public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
    try {
      userService.registerUser(registrationBody);
      return ResponseEntity.ok().build();
    } catch (UserAlreadyExistsException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (EmailFailureException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Post Mapping to handle user logins to provide authentication token.
   * @param loginBody The login information.
   * @return The authentication token if successful.
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
    String jwt = null;
    try {
      jwt = userService.loginUser(loginBody);
    } catch (UserNotVerifiedException ex) {
      LoginResponse response = new LoginResponse();
      response.setSuccess(false);
      String reason = "USER_NOT_VERIFIED";
      if (ex.isNewEmailSent()) {
        reason += "_EMAIL_RESENT";
      }
      response.setFailureReason(reason);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    } catch (EmailFailureException ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    if (jwt == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } else {
      LoginResponse response = new LoginResponse();
      response.setJwt(jwt);
      response.setSuccess(true);
      return ResponseEntity.ok(response);
    }
  }

  /**
   * Post mapping to verify the email of an account using the emailed token.
   * @param token The token emailed for verification. This is not the same as a
   *              authentication JWT.
   * @return 200 if successful. 409 if failure.
   */
  @PostMapping("/verify")
  public ResponseEntity verifyEmail(@RequestParam String token) {
    if (userService.verifyUser(token)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
  }

  /**
   * Gets the profile of the currently logged-in user and returns it.
   * @param user The authentication principal object.
   * @return The user profile.
   */
  @GetMapping("/me")
  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
    return user;
  }

}