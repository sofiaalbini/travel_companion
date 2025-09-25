package com.tourmate.service;

import com.tourmate.entity.UserAccount;
import com.tourmate.entity.Preference;
import com.tourmate.repository.PreferenceRepository;
import com.tourmate.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing users and their preferences.
 * Handles all business logic related to authentication, registration, and preferences.
 */
@Service
public class TourmateService {

    private final UserAccountRepository users;
    private final PreferenceRepository prefs;

    /**
     * Constructor for TourmateService.
     *
     * @param users repository for UserAccount entities
     * @param prefs repository for Preference entities
     */
    public TourmateService(UserAccountRepository users, PreferenceRepository prefs) {
        this.users = users;
        this.prefs = prefs;
    }

    // ---------- USERS ----------

    /**
     * Creates a new user in the system.
     *
     * @param username the username of the new user
     * @param hashedPassword the hashed password
     * @return the saved UserAccount entity
     */
    public UserAccount createUser(String username, String hashedPassword) {
        UserAccount user = new UserAccount(username, hashedPassword);
        return users.save(user);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the UserAccount if found
     */
    public Optional<UserAccount> getUser(String username) {
        return users.findById(username);
    }

    /**
     * Checks if a user exists by username.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        return users.existsById(username);
    }

    // ---------- PREFERENCES ----------

    /**
     * Adds a list of preferences to a user.
     *
     * @param user the user to add preferences for
     * @param preferences list of preference strings
     * @return the saved Preference entity
     */
    public Preference addPreferences(UserAccount user, List<String> preferences) {
        Preference pref = new Preference(user, preferences);
        return prefs.save(pref);
    }

    /**
     * Retrieves all preferences for a user.
     *
     * @param user the user to fetch preferences for
     * @return list of Preference entities
     */
    public List<Preference> getPreferences(UserAccount user) {
        return prefs.findByUser(user);
    }

    /**
     * Deletes all preferences for a user.
     *
     * @param user the user whose preferences will be deleted
     */
    public void resetPreferences(UserAccount user) {
        prefs.deleteByUser(user);
    }
}
