package com.tourmate.repository;

import com.tourmate.Preference;
import com.tourmate.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing Preference entities.
 * Provides CRUD operations and custom query to find preferences by user.
 */
@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
      /**
     * Find all preferences associated with a specific user.
     *
     * @param user the UserAccount entity
     * @return a list of Preference objects for the user
     */
    List<Preference> findByUser(UserAccount user);
}

