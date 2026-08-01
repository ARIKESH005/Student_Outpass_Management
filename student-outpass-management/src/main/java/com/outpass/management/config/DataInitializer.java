package com.outpass.management.config;

import com.outpass.management.entity.Role;
import com.outpass.management.entity.User;
import com.outpass.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String[] departments = {"ece", "cse", "eee", "aids", "aiml", "mech", "agri"};

            for (String dept : departments) {
                String adminUsername = "admin_" + dept;
                if (!userRepository.existsByUsername(adminUsername)) {
                    User admin = new User();
                    admin.setUsername(adminUsername);
                    admin.setEmail(adminUsername + "@college.edu");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRole(Role.ADMIN);
                    admin.setDepartment(dept.toUpperCase());
                    userRepository.save(admin);
                    System.out.println("Default Admin User created: " + adminUsername + " / admin123 for department " + dept.toUpperCase());
                }
            }
        };
    }
}
