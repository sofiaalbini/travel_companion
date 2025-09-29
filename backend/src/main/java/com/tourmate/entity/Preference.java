// Preference.java  --> tabella "preferences" con FK su users.username
package com.tourmate.entity;

import jakarta.persistence.*;

import java.util.List;

import com.tourmate.converter.StringListConverter;

/**
 * JPA entity representing a user's preferences.
 * Each Preference belongs to a UserAccount and stores a list of selected
 * options.
 */
@Entity
@Table(name = "preferences")
public class Preference {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true, nullable = false)
  private UserAccount user;

  @Column(name = "preferences", columnDefinition = "TEXT", nullable = false)
  @Convert(converter = StringListConverter.class)
  private List<String> preferences;

  public Preference() {}

  public Preference(UserAccount user, List<String> preferences) {
    this.user = user;
    this.preferences = preferences;
  }

  public Long getId() { return id; }

  public UserAccount getUser() { return user; }
  public void setUser(UserAccount user) { this.user = user; }

  public List<String> getPreferences() { return preferences; }
  public void setPreferences(List<String> preferences) { this.preferences = preferences; }
}
