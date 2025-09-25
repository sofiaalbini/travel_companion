package com.tourmate.config;

import com.tourmate.service.TourmateService;
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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security configuration class.
 * Uses TourmateService to fetch user data, configure authentication, CORS, and
 * security rules.
 */
@Configuration
@ConfigurationProperties(prefix = "cors")
public class SecurityConfig {

  private List<String> allowedOrigins = new ArrayList<>();

  public void setAllowedOrigins(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  private final TourmateService service;

  // Inject the service instead of repository
  public SecurityConfig(TourmateService service) {
    this.service = service;
  }

  /**
   * Password encoder bean for hashing passwords.
   *
   * @return BCryptPasswordEncoder instance
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Loads user details from the service for authentication.
   *
   * @return UserDetailsService to retrieve users
   */
  @Bean
  UserDetailsService userDetailsService() {
    return username -> service.getUser(username)
        .map(u -> User.withUsername(u.getUsername())
            .password(u.getPasswordHash())
            .roles("USER")
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  /**
   * Configures CORS for the SPA frontend.
   *
   * @return CORS configuration source
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(allowedOrigins);
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  /**
   * Configures HTTP security: CORS, CSRF, endpoint authorization, and basic auth.
   *
   * @param http HttpSecurity object
   * @return configured SecurityFilterChain
   * @throws Exception on configuration errors
   */
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        // In dev, using Basic Auth: CSRF is disabled.
        // For session/form login, consider enabling CSRF with
        // CookieCsrfTokenRepository.
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Registration endpoint is public
            .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
            // Preferences endpoints require authentication
            .requestMatchers("/api/preferences/**").authenticated()
            // All other endpoints are open in development; restrict later if needed
            .anyRequest().permitAll())
        // Simple HTTP Basic authentication for SPA/dev testing
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}
