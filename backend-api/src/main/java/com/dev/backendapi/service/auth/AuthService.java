package com.dev.backendapi.service.auth;

import com.dev.backendapi.controller.dto.AuthRequest.LoginRequest;
import com.dev.backendapi.controller.dto.AuthResponse;

/**
 * Authentication service interface
 * Handles core authentication business logic
 */
public interface AuthService {

    /**
     * Authenticate user with email and password
     *
     * @param request the login request containing credentials
     * @return AuthResponse containing tokens and user information
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Validate login request data
     *
     * @param request the login request to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validateLoginRequest(LoginRequest request);

    /**
     * Send password reset OTP to user email
     *
     * @param email the email address to send OTP to
     */
    void sendPasswordResetOtp(String email);

    /**
     * Reset user password using OTP
     *
     * @param email the email address
     * @param otp the one-time password
     * @param newPassword the new password
     */
    void resetPassword(String email, String otp, String newPassword);

    /**
     * Logout user by invalidating tokens
     *
     * @param email the email of the user to logout
     */
    void logout(String email);
}
