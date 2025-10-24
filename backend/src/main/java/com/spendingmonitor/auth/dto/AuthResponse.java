package com.spendingmonitor.auth.dto;

public record AuthResponse(String accessToken, long expiresInSeconds) {
}
