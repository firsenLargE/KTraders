package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User adminUser = new User("admin", "admin@kapiltraders.com", "admin123");
            userRepository.save(adminUser);
            System.out.println("Created default admin user: admin@kapiltraders.com / admin123");
        }
    }
}


