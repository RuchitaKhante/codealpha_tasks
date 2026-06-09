package com.stocktrading.controller;

import com.stocktrading.model.User;
import com.stocktrading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                body.get("username"),
                body.get("email"),
                body.get("password")
            );
            return ResponseEntity.ok(sanitize(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            User user = userService.login(body.get("username"), body.get("password"));
            return ResponseEntity.ok(sanitize(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sanitize(userService.getUserById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Strip password from response
    private Map<String, Object> sanitize(User user) {
        return Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "cashBalance", user.getCashBalance()
        );
    }
}
