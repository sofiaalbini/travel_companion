// UserAccount.java  --> tabella "users"
package com.tourmate.entity;

import jakarta.persistence.*;

/**
 * JPA entity representing a registered user.
 * Stores username and BCrypt-hashed password in the "users" table.
 */
@Entity
@Table(name = "users")
public class UserAccount {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  
    
    @Column(unique = true, nullable = false)
    private String username;  // Username is unique but not PK
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    // Constructors
    public UserAccount() {}

  /**
   * Constructs a UserAccount with a username and hashed password.
   *
   * @param username     unique username
   * @param passwordHash BCrypt-hashed password
   */
  public UserAccount(String username, String passwordHash) {
    this.username = username;
    this.passwordHash = passwordHash;
  }


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
}
