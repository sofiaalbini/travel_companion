package com.tourmate.service;

import com.tourmate.entity.UserAccount;
// import com.tourmate.controller.ApiController;
import com.tourmate.entity.Preference;
import com.tourmate.repository.PreferenceRepository;
import com.tourmate.repository.UserAccountRepository;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing users and their preferences.
 * Handles all business logic related to authentication, registration, and
 * preferences.
 */
@Service
public class TourmateService {
    // private static final Logger logger =
    // LoggerFactory.getLogger(TourmateService.class);

    private final UserAccountRepository userRepository;
    private final PreferenceRepository preferenceRepository;

    public TourmateService(UserAccountRepository userRepository, PreferenceRepository preferenceRepository) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
    }


    // ---------- USERS ----------
    public boolean userExists(String username) {
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            // logger.error("Error checking if user exists: {}", e.getMessage(), e);
            throw e;
        }
    }

    public UserAccount createUser(String username, String passwordHash) {
        try {
            // logger.info("Creating user: '{}'", username);

            UserAccount user = new UserAccount();
            user.setUsername(username);
            user.setPasswordHash(passwordHash);

            UserAccount savedUser = userRepository.save(user);
            // logger.info("User created successfully with ID: {}", savedUser.getId());

            return savedUser;
        } catch (Exception e) {
            // logger.error("Error creating user '{}': {}", username, e.getMessage(), e);
            throw e;
        }
    }

    public Optional<UserAccount> getUser(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            // logger.error("Error getting user '{}': {}", username, e.getMessage(), e);
            throw e;
        }
    }


    public Preference createPreferences(UserAccount user, List<String> preferences) {
        Preference pref = new Preference();
        pref.setUser(user);
        pref.setPreferences(preferences);
        return preferenceRepository.save(pref);
    }

    public Optional<Preference> getPreferenceById(Long id) {
        return preferenceRepository.findById(id);
    }

    public Preference savePreferences(Preference pref) {
        return preferenceRepository.save(pref);
    }

    public List<Preference> getPreferences(UserAccount user) {
        return preferenceRepository.findByUser(user);
    }
}