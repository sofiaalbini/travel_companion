// UserAccount.java
package com.tourmate.entity;

import jakarta.persistence.*;

/**
 * JPA entity representing a registered user. Stores username and BCrypt-hashed
 * password in the "users" table.
 */
@Entity
@Table(name = "users")
public class UserAccount {

    /**
     * Primary key of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Unique username of the user. Cannot be null.
     */
    @Column(unique = true, nullable = false)
    private String username;
    /**
     * BCrypt-hashed password of the user. Cannot be null.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * One-to-One relationship with Preference. Cascade REMOVE ensures
     * preferences are deleted when the user is deleted.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Preference preference;

    /**
     * Default constructor.
     */
    public UserAccount() {}

    /**
     * Constructs a UserAccount with a username and hashed password.
     *
     * @param username unique username of the user
     * @param passwordHash BCrypt-hashed password
     */
    public UserAccount(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    //getters and setters

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }
}
