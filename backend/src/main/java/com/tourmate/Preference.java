// Preference.java  --> tabella "preferences" con FK su users.username
package com.example.tourmate;

import jakarta.persistence.*;

import com.example.tourmate.converter.StringListConverter;
import java.util.List;

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

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
  private UserAccount user;

  @Column(name = "preferences", columnDefinition = "TEXT", nullable = false)
  @Convert(converter = StringListConverter.class)
  private List<String> preferences;

  /**
   * Default constructor required by JPA.
   */
  public Preference() {
  }

  /**
     * Constructs a Preference for a user with a list of selected options.
     *
     * @param user the owner of the preferences
     * @param preferences list of selected preference strings
     */
  public Preference(UserAccount user, List<String> preferences) {
    this.user = user;
    this.preferences = preferences;
  }

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
    this.preferences = preferences;
  }
}
