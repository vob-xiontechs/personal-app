package com.dev.backendapi.controller.dto;

import com.dev.backendapi.constants.auth.AuthConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

/**
 * Comprehensive Authentication Response DTO
 * Manages all types of authentication responses (Login, Logout, Refresh, etc.)
 * Uses centralized constants for all messages
 */
@Data
@Builder
public class AuthResponse {

    private String message;
    private String status; // SUCCESS, ERROR, WARNING
    private Object data; // Nested data object for login responses, or other objects for other responses

    /**
     * Inner class for authentication data
     */
    @Data
    @Builder
    public static class AuthData {
        private String email;
        private String userId;
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private Long refreshExpiresIn;
        private Boolean success;

        // Explicit getter methods for Lombok compatibility
        public String getEmail() { return email; }
        public String getUserId() { return userId; }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public String getTokenType() { return tokenType; }
        public Long getExpiresIn() { return expiresIn; }
        public Long getRefreshExpiresIn() { return refreshExpiresIn; }
        public Boolean getSuccess() { return success; }
    }

    // ===== LOGIN RESPONSES =====

    /**
     * Create success response for login
     */
    public static AuthResponse createLoginSuccess(String email, String userId,
                                                  String accessToken, String refreshToken,
                                                  Long expiresIn, Long refreshExpiresIn) {
        return AuthResponse.builder()
                .message(AuthConstants.LOGIN_SUCCESSFUL)
                .status("SUCCESS")
                .data(AuthData.builder()
                        .email(email)
                        .userId(userId)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(expiresIn)
                        .refreshExpiresIn(refreshExpiresIn)
                        .success(true)
                        .build())
                .build();
    }

    /**
     * Create login response with additional user data
     */
    public static AuthResponse createLoginSuccessWithData(String email, String userId,
                                                          String accessToken, String refreshToken,
                                                          Long expiresIn, Long refreshExpiresIn,
                                                          Object userData) {
        return AuthResponse.builder()
                .message(AuthConstants.LOGIN_SUCCESSFUL)
                .status("SUCCESS")
                .data(AuthData.builder()
                        .email(email)
                        .userId(userId)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(expiresIn)
                        .refreshExpiresIn(refreshExpiresIn)
                        .success(true)
                        .build())
                .build();
    }

    // ===== TOKEN REFRESH RESPONSES =====

    /**
     * Create success response for token refresh
     */
    public static AuthResponse createRefreshSuccess(String accessToken, Long expiresIn) {
        return AuthResponse.builder()
                .message(AuthConstants.TOKEN_REFRESH_SUCCESSFUL)
                .status("SUCCESS")
                .data(AuthData.builder()
                        .accessToken(accessToken)
                        .tokenType("Bearer")
                        .expiresIn(expiresIn)
                        .success(true)
                        .build())
                .build();
    }

    /**
     * Create refresh response with new refresh token
     */
    public static AuthResponse createRefreshSuccessWithNewTokens(String accessToken, String refreshToken,
                                                                 Long expiresIn, Long refreshExpiresIn) {
        return AuthResponse.builder()
                .message(AuthConstants.TOKENS_REFRESH_SUCCESSFUL)
                .status("SUCCESS")
                .data(AuthData.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(expiresIn)
                        .refreshExpiresIn(refreshExpiresIn)
                        .success(true)
                        .build())
                .build();
    }

    // ===== LOGOUT RESPONSES =====

    /**
     * Create success response for logout
     */
    public static AuthResponse createLogoutSuccess() {
        return AuthResponse.builder()
                .message(AuthConstants.LOGOUT_SUCCESSFUL)
                .status("SUCCESS")
                .build();
    }

    /**
     * Create logout response with session info
     */
    public static AuthResponse createLogoutSuccessWithInfo(Object sessionInfo) {
        return AuthResponse.builder()
                .message(AuthConstants.LOGOUT_SUCCESSFUL)
                .status("SUCCESS")
                .data(sessionInfo)
                .build();
    }

    // ===== ERROR RESPONSES =====

    /**
     * Create error response for authentication failures
     */
    public static AuthResponse createAuthError(String message) {
        return AuthResponse.builder()
                .message(message)
                .status("ERROR")
                .build();
    }

    /**
     * Create error response with details
     */
    public static AuthResponse createAuthErrorWithDetails(String message, Object errorDetails) {
        return AuthResponse.builder()
                .message(message)
                .status("ERROR")
                .data(errorDetails)
                .build();
    }

    // ===== VALIDATION RESPONSES =====

    /**
     * Create validation error response
     */
    public static AuthResponse createValidationError(String message, Object validationErrors) {
        return AuthResponse.builder()
                .message(message)
                .status("VALIDATION_ERROR")
                .data(validationErrors)
                .build();
    }

    // ===== PASSWORD RESET RESPONSES =====

    /**
     * Create password reset OTP sent response
     */
    public static AuthResponse createPasswordResetOtpSent(String email) {
        return AuthResponse.builder()
                .message("Password reset OTP sent successfully")
                .status("SUCCESS")
                .data("OTP sent to registered email")
                .build();
    }

    /**
     * Create password reset success response
     */
    public static AuthResponse createPasswordResetSuccess() {
        return AuthResponse.builder()
                .message("Password reset successful")
                .status("SUCCESS")
                .build();
    }

    // ===== GETTER METHODS FOR BACKWARD COMPATIBILITY =====

    @JsonIgnore
    public String getEmail() {
        return data instanceof AuthData ? ((AuthData) data).getEmail() : null;
    }

    @JsonIgnore
    public String getUserId() {
        return data instanceof AuthData ? ((AuthData) data).getUserId() : null;
    }

    @JsonIgnore
    public String getAccessToken() {
        return data instanceof AuthData ? ((AuthData) data).getAccessToken() : null;
    }

    @JsonIgnore
    public String getRefreshToken() {
        return data instanceof AuthData ? ((AuthData) data).getRefreshToken() : null;
    }

    @JsonIgnore
    public String getTokenType() {
        return data instanceof AuthData ? ((AuthData) data).getTokenType() : null;
    }

    @JsonIgnore
    public Long getExpiresIn() {
        return data instanceof AuthData ? ((AuthData) data).getExpiresIn() : null;
    }

    @JsonIgnore
    public Long getRefreshExpiresIn() {
        return data instanceof AuthData ? ((AuthData) data).getRefreshExpiresIn() : null;
    }

    // ===== UTILITY METHODS =====

    /**
     * Check if response is successful
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(this.status);
    }

    /**
     * Check if response has tokens
     */
    public boolean hasTokens() {
        String token = getAccessToken();
        return token != null && !token.trim().isEmpty();
    }

    /**
     * Check if response has refresh token
     */
    public boolean hasRefreshToken() {
        String token = getRefreshToken();
        return token != null && !token.trim().isEmpty();
    }

    /**
     * Get token expiration info
     */
    public String getTokenExpirationInfo() {
        Long exp = getExpiresIn();
        if (exp == null) return null;
        long hours = exp / 3600000;
        long minutes = (exp % 3600000) / 60000;
        return String.format("%dh %dm", hours, minutes);
    }
}
