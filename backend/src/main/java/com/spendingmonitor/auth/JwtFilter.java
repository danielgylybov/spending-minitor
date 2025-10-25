package com.spendingmonitor.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.spendingmonitor.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private JwtService jwtService;
    private UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        String path = req.getRequestURI();

        if (auth == null || !auth.startsWith("Bearer ")) {
            log.debug("No valid Authorization header found for {}", path);
            chain.doFilter(req, res);
            return;
        }

        String token = auth.substring(7);
        try {
            var jws = jwtService.parse(token);
            Claims claims = jws.getBody();
            UUID userId = UUID.fromString(claims.getSubject());
            log.debug("Parsed token for userId={}", userId);

            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                log.warn("User not found for id={}", userId);
            } else if (!user.isEnabled()) {
                log.warn("User {} is disabled", user.getEmail());
            } else {
                var authToken = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authenticated user {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Invalid JWT for {}: {}", path, e.getMessage());
        }

        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        boolean skip = p.startsWith("/api/auth/");
        if (skip) log.trace("Skipping JWT filter for {}", p);
        return skip;
    }
}
