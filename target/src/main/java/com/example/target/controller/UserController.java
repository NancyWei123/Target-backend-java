package com.example.target.controller;

import com.example.target.dto.LoginRequest;
import com.example.target.entity.User;
import com.example.target.repository.UserRepository;
import com.example.target.tools.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ✅ REGISTER
    @PostMapping("/register")
    public User register(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        // ✅ Encrypt password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // ✅ Compare raw password with encrypted password
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return JwtUtil.generateToken(user.getId());
    }

    // ✅ GET CURRENT USER INFO
    @GetMapping
    public User getUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ UPDATE USER INFO
    @PutMapping
    public User updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody User updatedUser) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        return userRepository.save(user);
    }

    // ✅ CHANGE PASSWORD
    @PutMapping("/password")
    public String changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody LoginRequest request) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "Password updated";
    }

    // ✅ DELETE USER
    @DeleteMapping("/{id}")
    public String deleteUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        userRepository.deleteById(userId);
        return "User deleted";
    }

}