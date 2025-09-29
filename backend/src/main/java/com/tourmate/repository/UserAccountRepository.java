package com.tourmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourmate.entity.UserAccount;

import java.util.Optional;

/**
 * Repository interface for managing UserAccount entities.
 *  Provides custom methods to find a user
 * by username and check for username existence.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * Finds a UserAccount by its username.
     *
     * @param username the unique username of the user
     * @return an Optional containing the UserAccount if found, or empty if not
     */
    Optional<UserAccount> findByUsername(String username);

    /**
     * Checks whether a UserAccount with the given username exists.
     *
     * @param username the username to check
     * @return true if a UserAccount with the given username exists, false
     * otherwise
     */
    boolean existsByUsername(String username);
}