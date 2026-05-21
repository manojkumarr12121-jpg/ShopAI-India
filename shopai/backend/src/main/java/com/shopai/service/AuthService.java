package com.shopai.service;

import com.shopai.dto.*;
import com.shopai.model.User;
import com.shopai.repository.UserRepository;
import com.shopai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid email or password");
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
            .token(token).userId(user.getUserId())
            .email(user.getEmail()).fullName(user.getFullName())
            .message("Login successful").build();
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");
        User user = User.builder()
            .fullName(req.getFullName()).email(req.getEmail())
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .phone(req.getPhone()).city(req.getCity())
            .state(req.getState()).pincode(req.getPincode())
            .isActive(true).build();
        userRepo.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
            .token(token).userId(user.getUserId())
            .email(user.getEmail()).fullName(user.getFullName())
            .message("Registration successful! Welcome to ShopAI India").build();
    }
}
