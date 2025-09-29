package com.tourmate.controller;

import com.tourmate.entity.UserAccount;
import com.tourmate.entity.Preference;
import com.tourmate.service.TourmateService;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final TourmateService service;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    public ApiController(TourmateService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    public record RegisterRequest(String username, String password) {
    }

    public record LoginRequest(String username, String password) {
    }

    public record PrefRequest(List<String> preferences) {
    }

    // ------------------- REGISTER -------------------
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        logger.info("Register attempt for username: {}", r.username());

        if (service.userExists(r.username())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        service.createUser(r.username(), passwordEncoder.encode(r.password()));
        logger.info("User registered successfully: {}", r.username());
        return ResponseEntity.ok().build();
    }

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

        // Create Spring Security principal
        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();

        // Set authentication in SecurityContext
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Persist SecurityContext in session
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        logger.info("Session created for user: '{}', session ID: {}", user.getUsername(), session.getId());

        return ResponseEntity.ok(user.getUsername());
    }

    // ------------------- CURRENT USER -------------------
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

    // ------------------- LOGOUT -------------------
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

    // ------------------- PREFERENCES -------------------
    @PostMapping("/preferences")
    public ResponseEntity<?> createPreferences(@AuthenticationPrincipal UserDetails user,
            @RequestBody PrefRequest r) {
        if (user == null)
            return ResponseEntity.status(401).body("Not logged in");
        if (r.preferences() == null || r.preferences().isEmpty())
            return ResponseEntity.badRequest().body("Select at least one preference");

        logger.info("Creating preferences for user: {}", user.getUsername());

        UserAccount u = service.getUser(user.getUsername()).orElseThrow();

        // Check if preferences already exist, don’t allow creation
        List<Preference> existingPrefs = service.getPreferences(u);
        if (!existingPrefs.isEmpty()) {
            return ResponseEntity.status(409).body("Preferences already exist. Use PUT to update.");
        }

        Preference p = service.createPreferences(u, r.preferences());
        return ResponseEntity.status(201).body(p);
    }

    @PutMapping("/preferences")
    public ResponseEntity<?> updatePreferences(@AuthenticationPrincipal UserDetails user,
            @RequestBody PrefRequest r) {
        if (user == null)
            return ResponseEntity.status(401).body("Not logged in");

        UserAccount u = service.getUser(user.getUsername()).orElseThrow();
        List<Preference> existingPrefs = service.getPreferences(u);

        if (existingPrefs.isEmpty()) {
            return ResponseEntity.status(404).body("No preferences found to update");
        }

        Preference pref = existingPrefs.get(0);
        pref.setPreferences(r.preferences());
        Preference updated = service.savePreferences(pref);

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences(@AuthenticationPrincipal UserDetails user) {
        if (user == null)
            return ResponseEntity.status(401).body("Not logged in");

        UserAccount u = service.getUser(user.getUsername()).orElseThrow();
        List<Preference> preferences = service.getPreferences(u);

        if (!preferences.isEmpty()) {
            // Return only the stored list of preferences
            return ResponseEntity.ok(preferences.get(0).getPreferences());
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
