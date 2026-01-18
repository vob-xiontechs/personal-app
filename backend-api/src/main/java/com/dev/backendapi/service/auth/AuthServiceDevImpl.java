package com.dev.backendapi.service.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.dev.backendapi.constants.auth.AuthConstants;
import com.dev.backendapi.controller.dto.AuthRequest.LoginRequest;
import com.dev.backendapi.controller.dto.AuthResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Development authentication service implementation
 * Provides dummy authentication for development/testing
 */
@Service
@Slf4j
@Profile("develop")  // Only load in develop profile
public class AuthServiceDevImpl implements AuthService {

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        log.debug("Development mode: Dummy authentication for user: {}", request.getEmail());

        // Create a dummy successful response for development
        return AuthResponse.createLoginSuccess(
            request.getEmail(),
            request.getEmail(),
            "dev-jwt-token-" + System.currentTimeMillis(),
            "dev-refresh-token-" + System.currentTimeMillis(),
            86400000L,  // 24 hours
            604800000L // 7 days
        );
    }

    @Override
    public void validateLoginRequest(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getPassword().length() < AuthConstants.MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + AuthConstants.MIN_PASSWORD_LENGTH + " characters");
        }
    }

    @Override
    public void sendPasswordResetOtp(String email) {
        log.info("Development mode: Password reset OTP request for email: {}", email);
        throw new UnsupportedOperationException("Password reset OTP not yet implemented");
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        log.info("Development mode: Password reset attempt for email: {}", email);
        throw new UnsupportedOperationException("Password reset not yet implemented");
    }

    @Override
    public void logout(String email) {
        log.info("Development mode: Logout request processed for user: {}", email);
    }
}
