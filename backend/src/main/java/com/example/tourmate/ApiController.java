// ApiController.java
package com.example.tourmate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

  private final UserAccountRepository users;
  private final PreferenceRepository prefs;
  private final PasswordEncoder enc;

  public ApiController(UserAccountRepository users, PreferenceRepository prefs, PasswordEncoder enc) {
    this.users = users; this.prefs = prefs; this.enc = enc;
  }

  // ---- AUTH ----
  public record RegisterRequest(String username, String password) {}

  @PostMapping("/auth/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
    if (users.existsById(r.username())) {
        return ResponseEntity.badRequest().body("Username già esistente");
    }
    UserAccount user = new UserAccount(r.username(), enc.encode(r.password()));
    users.save(user);
    return ResponseEntity.ok().build();
} 

  @GetMapping("/auth/me")
  public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails me) {
    return ResponseEntity.ok(me.getUsername());
  }

  // ---- PREFERENZE (protette) ----
  public record PrefRequest(List<String> preferenze) {}

@PostMapping("/preferences")
public ResponseEntity<?> createPref(@AuthenticationPrincipal UserDetails me,
                                    @RequestBody PrefRequest r) {
    if (r.preferenze() == null || r.preferenze().isEmpty()) {
        return ResponseEntity.badRequest().body("Devi selezionare almeno una preferenza");
    }

    UserAccount u = users.findById(me.getUsername()).orElseThrow();
    Preference p = prefs.save(new Preference(u, r.preferenze()));

    return ResponseEntity.ok(p);
}


  @GetMapping("/preferences")
  public List<Preference> myPrefs(@AuthenticationPrincipal UserDetails me) {
    UserAccount u = users.findById(me.getUsername()).orElseThrow();
    return prefs.findByUser(u);
  }
}
