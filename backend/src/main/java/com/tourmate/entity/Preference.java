// Preference.java
package com.tourmate.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


/**
 * JPA entity representing a user's preferences. 
 * * Table: "preferences" Columns: - id: primary key - user_id: foreign key
 * referencing users.id - preferences: list of preference strings.
 * Each Preference is linked to a single UserAccount and stores 
 * a list of selected options as PostgreSQL array.
 */
@Entity
@Table(name = "preferences")
public class Preference {

    /* Primary key of the Preference entity. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     /* One-to-One relationship with UserAccount. */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserAccount user;

    /* List of user-selected preferences. */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "preferences", columnDefinition = "text[]", nullable = false)
    private List<String> preferences = new ArrayList<>();;

    /**
     * Default constructor required by JPA.
     */
    public Preference() {
    }

    /**
     * Constructs a Preference for a given user with a list of preferences.
     *
     * @param user the UserAccount this preference belongs to
     * @param preferences list of strings representing user preferences
     */
    public Preference(UserAccount user, List<String> preferences) {
        this.user = user;
        this.preferences = preferences; 
    }

    // getters and setters
    
    public Long getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public List<String> getPreferences() {
        return preferences;
    }

   public void setPreferences(List<String> preferences) {
        this.preferences = preferences != null ? preferences : new ArrayList<>();
    }
}
