package com.dev.backendapi.service.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dev.backendapi.constants.auth.AuthConstants;
import com.dev.backendapi.controller.dto.AuthRequest.LoginRequest;
import com.dev.backendapi.controller.dto.AuthResponse;
import com.dev.backendapi.service.AppUserDetailsService;
import com.dev.backendapi.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication service implementation
 * Handles core authentication business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!develop")  // Only load when NOT in develop profile
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        log.debug("Authenticating user: {}", request.getEmail());

        // Perform authentication
        Authentication authentication = authenticate(request.getEmail(), request.getPassword());

        // Load user details
        final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());

        // Generate tokens
        final String accessToken = jwtUtil.generateSmartToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.debug("Authentication successful for user: {}", authentication.getName());

        // Build response
        return AuthResponse.createLoginSuccess(
            userDetails.getUsername(),
            userDetails.getUsername(),
            accessToken,
            refreshToken,
            jwtUtil.getExpirationTime(),
            jwtUtil.getRefreshExpirationTime()
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
        // TODO: Implement OTP generation and email sending
        log.info("Password reset OTP request for email: {}", email);
        throw new UnsupportedOperationException("Password reset OTP not yet implemented");
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        // TODO: Implement password reset with OTP verification
        log.info("Password reset attempt for email: {}", email);
        throw new UnsupportedOperationException("Password reset not yet implemented");
    }

    @Override
    public void logout(String email) {
        // TODO: Implement proper logout with token invalidation
        log.info("Logout request processed for user: {}", email);
    }

    /**
     * Perform authentication with proper error handling
     */
    private Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email.trim().toLowerCase(), password)
        );
    }
}
