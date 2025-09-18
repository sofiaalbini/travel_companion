// SecurityConfig.java
package com.example.tourmate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

  private final UserAccountRepository users;

  public SecurityConfig(UserAccountRepository users) {
    this.users = users;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Come Spring trova l'utente nel DB per autenticare le richieste
  @Bean
  UserDetailsService userDetailsService() {
    return username -> users.findById(username)
      .map(u -> User.withUsername(u.getUsername())
                    // usa il campo giusto della tua entity (es. getPasswordHash())
                    .password(u.getPasswordHash())
                    .roles("USER")
                    .build())
      .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
  }

  // CORS per l'SPA (modifica origin se diverso)
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of("http://localhost:4200"));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors(Customizer.withDefaults())
      // In dev, se usi Basic, puoi disabilitare CSRF.
      // Se passi a sessioni/formLogin, valuta di riabilitarlo (CookieCsrfTokenRepository).
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        // registrazione aperta
        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
        // preferenze protette
        .requestMatchers("/api/preferences/**").authenticated()
        // tutto il resto aperto in dev (puoi restringere in seguito)
        .anyRequest().permitAll()
      )
      // Autenticazione semplice per test e SPA
      .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}
