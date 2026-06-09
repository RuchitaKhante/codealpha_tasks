package com.stocktrading.service;

import com.stocktrading.model.User;
import com.stocktrading.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * UserService: Handles user registration, login, and balance management.
 * Each new user starts with $10,000 in virtual cash.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final double STARTING_BALANCE = 10000.00;

    // Register a new user
    public User register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }
        User user = new User(null, username, email, password, STARTING_BALANCE);
        return userRepository.save(user);
    }

    // Login: verify credentials and return user
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Update cash balance (called after buy/sell)
    public User updateBalance(User user, double newBalance) {
        user.setCashBalance(Math.round(newBalance * 100.0) / 100.0);
        return userRepository.save(user);
    }
}
