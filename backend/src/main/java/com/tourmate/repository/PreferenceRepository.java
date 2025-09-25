package com.tourmate.repository;

import com.tourmate.entity.Preference;
import com.tourmate.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    List<Preference> findByUser(UserAccount user);

    void deleteByUser(UserAccount user);

    // custom JPQL query for element collection
    @Query("SELECT p FROM Preference p JOIN p.preferences pref WHERE pref = :value")
    List<Preference> findByPreferenceValue(@Param("value") String value);

    @Query("SELECT p FROM Preference p WHERE p.user.username = :username")
    List<Preference> findByUsername(@Param("username") String username);
}
