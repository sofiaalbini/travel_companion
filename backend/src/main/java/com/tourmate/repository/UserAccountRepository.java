package com.tourmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourmate.entity.UserAccount;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for managing UserAccount entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    // /**
    //  * Optional: find users by partial username (for search/autocomplete).
    //  *
    //  * @param keyword substring to search in username
    //  * @return list of matching UserAccount entities
    //  */
    // List<UserAccount> findByUsernameContaining(String keyword);
    //  // Find user by username
    Optional<UserAccount> findByUsername(String username);
    
    // Check if user exists by username
    boolean existsByUsername(String username);
}