package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository userRepository;
    public void signUp(User user) { userRepository.save(user); }
    public User login(String email, String password) { return userRepository.findByEmailAndPassword(email, password); }
    public User findByEmail(String email) { return userRepository.findByEmail(email); }
    public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }
}


