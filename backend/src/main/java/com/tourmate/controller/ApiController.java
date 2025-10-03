package com.tourmate.controller;

import com.tourmate.entity.UserAccount;
import com.tourmate.entity.Preference;
import com.tourmate.service.TourmateService;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.security.Principal;
import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;

/**
 * REST controller for Tourmate API. Handles user authentication (register,
 * login, logout) and preferences (create, update, retrieve) under "/api". Uses
 * {@link TourmateService} for business logic and {@link PasswordEncoder} for
 * password handling.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * Service layer for user and preference management.
     */
    private final TourmateService service;

    /**
     * Bean for hashing and verifying passwords.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Logger for request and event tracing.
     */
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    /**
     * Constructs the ApiController with required dependencies.
     *
     * @param service TourmateService to handle business logic
     * @param passwordEncoder PasswordEncoder bean for hashing/verifying
     * passwords
     */
    public ApiController(TourmateService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * DTO for user registration request.
     *
     * @param username the desired username
     * @param password the raw password
     */
    public record RegisterRequest(String username, String password) {

    }

    /**
     * DTO for user login request.
     *
     * @param username the username
     * @param password the raw password
     */
    public record LoginRequest(String username, String password) {

    }

    /**
     * DTO for preferences request.
     *
     * @param preferences list of selected preferences
     */
    public record PrefRequest(List<String> preferences) {

    }

    /**
     * DTO for preference response.
     *
     * @param id the preference ID
     * @param preferences list of selected preferences
     */
    public record PreferenceResponse(Long id, List<String> preferences) {

        public static PreferenceResponse from(Preference p) {
            return new PreferenceResponse(p.getId(), p.getPreferences());
        }
    }

    // ---------- USERS ENDPOINTS----------
    /**
     * Registers a new user.
     *
     * @param r RegisterRequest containing username and raw password
     * @return 201 Created if successful, 409 if username already
     * exists
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        logger.info("Register attempt for username: {}", r.username());

        if (service.userExists(r.username())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body("Username already exists");
    }
        service.createUser(r.username(), passwordEncoder.encode(r.password()));
        logger.info("User registered successfully: {}", r.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Authenticates a user and creates a session.
     *
     * @param r LoginRequest containing username and password
     * @param request HttpServletRequest used to create session
     * @return 200 OK with username if successful, 401 Unauthorized if
     * credentials are invalid
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r, HttpServletRequest request) {
        logger.info("Login attempt for username: '{}'", r.username());

        // Trim whitespace from username
        String trimmedUsername = r.username() != null ? r.username().trim() : "";

        Optional<UserAccount> userOpt = service.getUser(trimmedUsername);
        if (userOpt.isEmpty()) {
            logger.warn("User not found: '{}'", trimmedUsername);
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        UserAccount user = userOpt.get();
        logger.debug("Found user: '{}', checking password", user.getUsername());

        if (!passwordEncoder.matches(r.password(), user.getPasswordHash())) {
            logger.warn("Invalid password for user: '{}'", trimmedUsername);
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        logger.info("Password verified for user: '{}'", user.getUsername());

        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();

        // set authentication in SecurityContext
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // persist SecurityContext in session
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        logger.info("Session created for user: '{}', session ID: {}", user.getUsername(), session.getId());

        // return ResponseEntity.ok(user.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Returns the currently authenticated user's username.
     *
     * @param user authenticated UserDetails injected by Spring Security
     * @param request HttpServletRequest for session inspection
     * @return 200 OK with username or 401 Unauthorized if not logged in
     */
    @GetMapping("/auth/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails user, HttpServletRequest request) {
        logger.debug("Me endpoint called");

        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.debug("Session found: {}", session.getId());
        } else {
            logger.debug("No session found");
        }

        if (user == null) {
            logger.warn("No authenticated user found");
            return ResponseEntity.status(401).body("Not logged in");
        }

        logger.debug("Authenticated user: {}", user.getUsername());
        return ResponseEntity.ok(user.getUsername());
    }

    /**
     * Logs out the currently authenticated user by invalidating the session and
     * clearing the SecurityContext.
     *
     * @param request HttpServletRequest used to access session
     * @return 200 OK after successful logout
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("Invalidating session: {}", session.getId());
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    // ---------- PREFERENCES ENDPOINTS ----------
    /**
     * Creates new preferences for the authenticated user. Returns 409 Conflict
     * if preferences already exist.
     *
     * @param request PrefRequest containing list of preferences
     * @param principal authenticated user principal
     * @return 201 Created with PreferenceResponse, 400 if invalid input, 409 if
     * preferences already exist
     */
    @PostMapping("/preferences")
    public ResponseEntity<PreferenceResponse> createPreferences(@RequestBody PrefRequest request, Principal principal) {
        logger.info("Creating preferences for user: {}", principal.getName());

        // Validate input
        if (request.preferences() == null || request.preferences().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Get the user first
        UserAccount user = service.getUser(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if preferences already exist
        if (service.getPreferenceForUser(user).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        }

        // Create preferences
        Preference pref = service.createPreferences(user, request.preferences());

        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
                .body(PreferenceResponse.from(pref));
    }

    /**
     * Updates existing preferences for the authenticated user. Returns 404 Not
     * Found if no preferences exist yet.
     *
     * @param request PrefRequest containing updated list of preferences
     * @param principal authenticated user principal
     * @return 200 OK with updated PreferenceResponse, 400 if invalid input, 404
     * if no preferences exist
     */
    @PutMapping("/preferences")
    public ResponseEntity<PreferenceResponse> updatePreferences(@RequestBody PrefRequest request, Principal principal) {
        logger.info("Updating preferences for user: {}", principal.getName());

        // Validate input
        if (request.preferences() == null || request.preferences().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Get the user first
        UserAccount user = service.getUser(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Preference pref = service.updatePreferences(user, request.preferences());
            return ResponseEntity.ok(PreferenceResponse.from(pref));
        } catch (RuntimeException e) {
            // If preferences don't exist
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * Retrieves preferences for the authenticated user. Returns an empty list
     * if no preferences have been set yet.
     *
     * @param principal authenticated user principal
     * @return 200 OK with list of preference strings (empty if none exist)
     */
    @GetMapping("/preferences")
    public ResponseEntity<List<String>> getPreferences(Principal principal) {
        logger.info("Fetching preferences for user: {}", principal.getName());

        UserAccount user = service.getUser(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Preference> preference = service.getPreferenceForUser(user);

        if (preference.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(preference.get().getPreferences());
    }
}
