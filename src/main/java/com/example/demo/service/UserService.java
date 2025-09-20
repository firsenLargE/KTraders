package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {
    void signUp(User user);
    User login(String email, String password);
    User findByEmail(String email);
    boolean existsByEmail(String email);
}


