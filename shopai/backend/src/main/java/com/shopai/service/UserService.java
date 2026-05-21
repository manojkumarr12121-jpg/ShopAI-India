package com.shopai.service;

import com.shopai.model.User;
import com.shopai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service @RequiredArgsConstructor @Slf4j
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) { return userRepo.findByEmail(email); }
    public Optional<User> findById(Long id)          { return userRepo.findById(id); }
    public boolean existsByEmail(String email)        { return userRepo.existsByEmail(email); }

    @Transactional
    public User register(String fullName, String email, String rawPassword,
                         String phone, String city, String state, String pincode) {
        if (existsByEmail(email))
            throw new RuntimeException("Email already registered: " + email);
        User user = User.builder()
                .fullName(fullName).email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .phone(phone).city(city).state(state).pincode(pincode)
                .role(User.Role.CUSTOMER).build();
        User saved = userRepo.save(user);
        log.info("New user registered: {} ({})", fullName, email);
        return saved;
    }

    @Transactional
    public User updateProfile(Long userId, User updated) {
        return userRepo.findById(userId).map(u -> {
            u.setFullName(updated.getFullName());
            u.setPhone(updated.getPhone());
            u.setCity(updated.getCity());
            u.setState(updated.getState());
            u.setPincode(updated.getPincode());
            u.setAddress(updated.getAddress());
            return userRepo.save(u);
        }).orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    @Transactional
    public void addLoyaltyPoints(Long userId, int points) {
        userRepo.findById(userId).ifPresent(u -> {
            u.setLoyaltyPoints(u.getLoyaltyPoints() + points);
            userRepo.save(u);
        });
    }
}
