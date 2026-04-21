package com.wafa.assurance.config;

import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            System.out.println("DataSeeder running, user count: " + userRepository.count());
            if (userRepository.count() == 0) {
                System.out.println("Creating users...");
                User admin = new User();
                admin.setEmail("admin@si-expert.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNom("Admin");
                admin.setPrenom("Système");
                admin.setRole("ADMIN");
                admin.setActif(true);
                userRepository.save(admin);

                User expert = new User();
                expert.setEmail("expert@si-expert.com");
                expert.setPassword(passwordEncoder.encode("admin123"));
                expert.setNom("Expert");
                expert.setPrenom("Assurance");
                expert.setRole("EXPERT");
                expert.setActif(true);
                userRepository.save(expert);
                System.out.println("Users created");
            } else {
                System.out.println("Users already exist");
            }
        } catch (Exception e) {
            System.out.println("DataSeeder error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
