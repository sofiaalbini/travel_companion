// UserAccount.java  --> tabella "users"
package com.example.tourmate;

import jakarta.persistence.*;

@Entity @Table(name = "users")
public class UserAccount {
  @Id
  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(nullable = false, name = "password_hash")
  private String passwordHash;

  public UserAccount() {}
  public UserAccount(String username, String passwordHash) {
    this.username = username; this.passwordHash = passwordHash;
  }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
