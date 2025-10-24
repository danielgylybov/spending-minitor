package com.spendingmonitor.auth;

import com.spendingmonitor.auth.dto.*;
import com.spendingmonitor.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    public void register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        var user = User.builder()
                .email(email)
                .fullName(req.fullName())
                .passwordHash(passwordEncoder.encode(req.password()))
                .enabled(true)
                .build();
        userRepo.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        var user = userRepo.findByEmailIgnoreCase(req.email().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!user.isEnabled() || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwt.createToken(user.getId(), user.getEmail());
        long expiresSeconds = 120L * 60L;
        return new AuthResponse(token, expiresSeconds);
    }
}
