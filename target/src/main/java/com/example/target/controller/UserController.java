package com.example.target.controller;

import com.example.target.dto.*;
import com.example.target.entity.User;
import com.example.target.repository.UserRepository;
import com.example.target.service.EmailService;
import com.example.target.service.VerificationCodeService;
import com.example.target.service.VerifyRequest;
import com.example.target.tools.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody String email) {
        email = email == null ? null : email.replace("\"", "").trim();
        System.out.println("Received email = [" + email + "]");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
        verificationCodeService.saveCode(email, code);
        emailService.sendVerificationEmail(email, code);

        return ResponseEntity.ok("Verification code sent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            return ResponseEntity.badRequest().body("Verification code is required");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest().body("New password is required");
        }
        boolean valid = verificationCodeService.verifyCode(request.getEmail(), request.getCode());
        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid or expired verification code");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        verificationCodeService.deleteCode(request.getEmail());
        return ResponseEntity.ok("Password reset successfully");
    }



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        System.out.println("Registering user: " + request.getEmail());
        System.out.println("Registering user: " + request.getCode());
        boolean valid = verificationCodeService.verifyCode(request.getEmail(), request.getCode());
        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid or expired verification code");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setIsActive(true);
        userRepository.save(user);
        verificationCodeService.deleteCode(request.getEmail());
        return ResponseEntity.ok("Registration successful");
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
    public String changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody ChangePassword changePassword) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // ✅ Compare raw password with encrypted password
        if (!encoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String encodedPassword = encoder.encode(changePassword.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "Password updated";
    }
    @PutMapping("/settings")
    public String updateSettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserSettingsDTO dto
    ) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDark_mode(dto.getDarkMode());
        user.setEmail_notifications(dto.getEmailNotifications());
        user.setTask_reminders(dto.getTaskReminders());
        userRepository.save(user);
        return "Success";
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