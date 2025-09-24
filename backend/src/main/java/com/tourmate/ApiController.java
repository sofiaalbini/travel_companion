// ApiController.java
package com.tourmate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// import community.flock.eco.feature.user.repositories.UserAccountRepository;
import com.tourmate.repository.UserAccountRepository;
import com.tourmate.repository.PreferenceRepository;

import java.util.List;

/**
 * REST controller for authentication and user preferences.
 * Provides endpoints for registration, login, and managing user preferences.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

  private final UserAccountRepository users;
  private final PreferenceRepository prefs;
  private final PasswordEncoder enc;

 /**
     * Constructor for dependency injection.
     *
     * @param users repository for accessing user accounts
     * @param prefs repository for accessing preferences
     * @param enc password encoder for hashing user passwords
     */
  public ApiController(UserAccountRepository users, PreferenceRepository prefs, PasswordEncoder enc) {
    this.users = users;
    this.prefs = prefs;
    this.enc = enc;
  }

  // ---- AUTH ----

  /**
     * Data Transfer Object for user registration.
     * Contains username and password fields sent by the client.
     */
  public record RegisterRequest(String username, String password) {
  }

   /**
     * Registers a new user account.
     *
     * @param r request object containing username and password
     * @return 400 Bad Request if username exists, 200 OK otherwise
     */
  @PostMapping("/auth/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
    if (users.existsById(r.username())) {
      return ResponseEntity.badRequest().body("Username already exists");
    }
    UserAccount user = new UserAccount(r.username(), enc.encode(r.password()));
    users.save(user);
    return ResponseEntity.ok().build();
  }

   /**
     * Returns the username of the currently authenticated user.
     *
     * @param me authenticated user details (injected by Spring Security)
     * @return username as a string
     */
  @GetMapping("/auth/me")
  public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails me) {
    return ResponseEntity.ok(me.getUsername());
  }

  // ---- PREFERENCES (requires authentication) ----

  /**
     * DTO for user preference requests.
     * Contains a list of selected preferences sent by the client.
     */
  public record PrefRequest(List<String> preferenze) {}

   /**
     * Saves a list of preferences for the authenticated user.
     *
     * @param me authenticated user details
     * @param r request object containing list of preferences
     * @return 400 Bad Request if list is empty, otherwise 200 OK with saved Preference object
     */
  @PostMapping("/preferences")
  public ResponseEntity<?> createPref(@AuthenticationPrincipal UserDetails me,
      @RequestBody PrefRequest r) {
    if (r.preferenze() == null || r.preferenze().isEmpty()) {
      return ResponseEntity.badRequest().body("At least one preference must be selected");
    }

    UserAccount u = users.findById(me.getUsername()).orElseThrow();
    Preference p = prefs.save(new Preference(u, r.preferenze()));

    return ResponseEntity.ok(p);
  }

  /**
     * Retrieves all preferences associated with the authenticated user.
     *
     * @param me authenticated user details
     * @return list of Preference objects
     */
  @GetMapping("/preferences")
  public List<Preference> myPrefs(@AuthenticationPrincipal UserDetails me) {
    UserAccount u = users.findById(me.getUsername()).orElseThrow();
    return prefs.findByUser(u);
  }
}
