package com.tourmate.service;

import com.tourmate.entity.UserAccount;
import com.tourmate.entity.Preference;
import com.tourmate.repository.PreferenceRepository;
import com.tourmate.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing users and their preferences. Handles all business
 * logic related to authentication, registration, and preferences.
 */
@Service
public class TourmateService {

    private final UserAccountRepository userRepository;
    private final PreferenceRepository preferenceRepository;

    public TourmateService(UserAccountRepository userRepository, PreferenceRepository preferenceRepository) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
    }

    // ---------- USERS ----------
    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Creates a new UserAccount with the given username and password hash.
     *
     * @param username the username
     * @param passwordHash BCrypt-hashed password
     * @return the saved UserAccount
     */
    public UserAccount createUser(String username, String passwordHash) {
        UserAccount user = new UserAccount(username, passwordHash);
        return userRepository.save(user);
    }

    /**
     * Retrieves a UserAccount by username.
     *
     * @param username the username
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserAccount> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    // ---------- PREFERENCES ----------
    /**
     * Creates preferences for a user.
     *
     * @param user the user
     * @param preferences list of preference strings
     * @return the saved Preference entity
     */
    @Transactional
    public Preference createPreferences(UserAccount user, List<String> preferences) {
        Preference pref = new Preference(user, preferences);
        Preference savedPref = preferenceRepository.save(pref);
        user.setPreference(savedPref); // sync bidirectional
        return savedPref;
    }

    
    /**
     * Updates existing preferences for a user.
     *
     * @param user the user whose preferences to update
     * @param preferences the new list of preferences
     * @return the updated Preference
     * @throws RuntimeException if preferences don't exist
     */
    @Transactional
    public Preference updatePreferences(UserAccount user, List<String> preferences) {
        Preference pref = getPreferenceForUser(user)
                .orElseThrow(() -> new RuntimeException("Preferences not found for user: " + user.getUsername()));

        pref.setPreferences(preferences);
        return preferenceRepository.save(pref);
    }

    /**
     * Retrieves preferences for a given user.
     *
     * @param user the user
     * @return Optional containing Preference if exists
     */
    public Optional<Preference> getPreferenceForUser(UserAccount user) {
        return preferenceRepository.findByUser(user);
    }

    /**
     * Saves or updates a Preference entity.
     *
     * @param pref the preference to save/update
     * @return the saved Preference
     */
    public Preference savePreferences(Preference pref) {
        return preferenceRepository.save(pref);
    }

    /**
     * Checks if a user has preferences.
     *
     * @param user the user
     * @return true if preferences exist, false otherwise
     */
    public boolean hasPreferences(UserAccount user) {
        return preferenceRepository.findByUser(user).isPresent();
    }

    /**
     * Retrieves a Preference by ID.
     *
     * @param id the preference ID
     * @return Optional containing Preference if found
     */
    public Optional<Preference> getPreferenceById(Long id) {
        return preferenceRepository.findById(id);
    }

    /**
     * Deletes a user and their preferences (cascade handles preference
     * deletion).
     *
     * @param userId the ID of the user to delete
     */
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
