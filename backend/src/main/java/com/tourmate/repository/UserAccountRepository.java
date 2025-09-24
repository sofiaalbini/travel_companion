package com.tourmate.repository;

import com.tourmate.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing UserAccount entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    // JpaRepository already provides save(), findById(), existsById(), etc.
}
