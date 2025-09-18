// Preference.java  --> tabella "preferences" con FK su users.username
package com.example.tourmate;

import jakarta.persistence.*;
import jakarta.persistence.Convert; 
import com.example.tourmate.converter.StringListConverter;
import java.util.List;


@Entity @Table(name = "preferences")
public class Preference {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
  private UserAccount user;

  
 @Column(name = "preferenze", columnDefinition = "TEXT", nullable = false)
 @Convert(converter = StringListConverter.class)
 private List<String> preferenze;

  public Preference() {}
  public Preference(UserAccount user, List <String> preferenze) {
    this.user = user; this.preferenze = preferenze;
  }

  public Long getId() { return id; }
  public UserAccount getUser() { return user; }
  public void setUser(UserAccount user) { this.user = user; }
  public List<String> getPreferenze() { return preferenze; }
  public void setPreferenze(List<String> preferenze) { this.preferenze = preferenze; } 
}

