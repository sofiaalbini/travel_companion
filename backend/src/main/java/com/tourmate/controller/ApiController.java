package com.tourmate.controller;

import com.tourmate.entity.UserAccount;
import com.tourmate.entity.Preference;
import com.tourmate.service.TourmateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for authentication and managing user preferences.
 * Handles registration, current user info, and preference management.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/ping")
    public String ping() {
        return "Backend is running 🚀";
    }

    private final TourmateService service;

    /**
     * Constructor for ApiController.
     *
     * @param service the service layer for users and preferences
     */
    public ApiController(TourmateService service) {
        this.service = service;
    }

    // ---------- AUTHENTICATION ----------

    public record RegisterRequest(String username, String password) {}

    /**
     * Registers a new user.
     *
     * @param r registration request containing username and password
     * @return HTTP 200 if successful, 400 if username already exists
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        if (service.userExists(r.username())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        service.createUser(r.username(), r.password());
        return ResponseEntity.ok().build();
    }

    /**
     * Returns the currently authenticated user's username.
     *
     * @param me authenticated UserDetails
     * @return the username of the logged-in user
     */
    @GetMapping("/auth/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails me) {
        return ResponseEntity.ok(me.getUsername());
    }

    // ---------- PREFERENCES (requires authentication) ----------

    public record PrefRequest(List<String> preferences) {}

    /**
     * Adds preferences for the authenticated user.
     *
     * @param me authenticated UserDetails
     * @param r preferences request
     * @return saved Preference entity or 400 if empty
     */
    @PostMapping("/preferences")
    public ResponseEntity<?> createPref(@AuthenticationPrincipal UserDetails me,
                                        @RequestBody PrefRequest r) {
        if (r.preferences() == null || r.preferences().isEmpty()) {
            return ResponseEntity.badRequest().body("At least one preference must be selected");
        }

        UserAccount u = service.getUser(me.getUsername()).orElseThrow();
        Preference p = service.addPreferences(u, r.preferences());

        return ResponseEntity.ok(p);
    }

    /**
     * Retrieves preferences of the authenticated user.
     *
     * @param me authenticated UserDetails
     * @return list of Preference entities
     */
    @GetMapping("/preferences")
    public List<Preference> myPrefs(@AuthenticationPrincipal UserDetails me) {
        UserAccount u = service.getUser(me.getUsername()).orElseThrow();
        return service.getPreferences(u);
        
        
    }
}
