package com.tourmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tourmate.entity.UserAccount;

import java.util.List;

/**
 * Repository interface for managing UserAccount entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    /**
     * Optional: find users by partial username (for search/autocomplete).
     *
     * @param keyword substring to search in username
     * @return list of matching UserAccount entities
     */
    List<UserAccount> findByUsernameContaining(String keyword);
}