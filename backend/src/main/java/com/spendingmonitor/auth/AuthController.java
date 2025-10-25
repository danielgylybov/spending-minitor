package com.spendingmonitor.auth;

import com.spendingmonitor.auth.dto.*;
import com.spendingmonitor.model.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest req) {
        log.info("Received register request for email: {}", req.email());
        try {
            auth.register(req);
            log.info("User registered successfully: {}", req.email());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Error during registration for {}: {}", req.email(), e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        log.info("Login attempt for email: {}", req.email());
        try {
            AuthResponse response = auth.login(req);
            log.info("Login successful for email: {}", req.email());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Login failed for {}: {}", req.email(), e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("Unauthorized /me request – no authenticated user found");
            return ResponseEntity.status(401).build();
        }

        log.debug("Returning /me info for user id: {}, email: {}", user.getId(), user.getEmail());
        return ResponseEntity.ok(new MeResponse(user.getId(), user.getEmail(), user.getFullName()));
    }
}
