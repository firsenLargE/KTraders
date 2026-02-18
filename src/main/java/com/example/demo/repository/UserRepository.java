package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    java.util.Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
