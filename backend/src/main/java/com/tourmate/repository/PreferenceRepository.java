package com.tourmate.repository;

import com.tourmate.entity.Preference;
import com.tourmate.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing Preference entities. Extends JpaRepository
 * to provide standard CRUD operations.
 */
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    /**
     * Finds a Preference by the associated UserAccount.
     *
     * @param user the UserAccount whose preferences are to be retrieved
     * @return an Optional containing the Preference if found, or empty if not
     */
    Optional<Preference> findByUser(UserAccount user);
}
