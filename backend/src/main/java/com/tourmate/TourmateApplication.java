// TourmateApplication.java
package com.tourmate;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import com.tourmate.entity.UserAccount;
// import com.tourmate.service.TourmateService;
// import java.util.Optional;

/**
 * Main entry point for the Tourmate Spring Boot application.
 */
@SpringBootApplication
public class TourmateApplication {
  public static void main(String[] args) {
    SpringApplication.run(TourmateApplication.class, args);
  }


      // Add this method to test login
    // @Bean
    // CommandLineRunner testLogin(TourmateService service, PasswordEncoder encoder) {
    //     return args -> {
    //         Optional<UserAccount> user = service.getUser("lisa test 17"); 
    //         if (user.isPresent()) {
    //             String hash = user.get().getPasswordHash();
    //             System.out.println("Stored hash: " + hash);
    //             System.out.println("Matches 'password': " + encoder.matches("123", hash));
    //         } else {
    //             System.out.println("User not found");
    //         }
    //     };
    // }
}
