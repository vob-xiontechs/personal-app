package com.dev.backendapi.constants.auth;

/**
 * Authentication Constants
 * Contains constants specific to authentication functionality
 */
public class AuthConstants {

    // Message Keys - Success Messages
    public static final String LOGIN_SUCCESSFUL = "Login successful";
    public static final String LOGOUT_SUCCESSFUL = "Logout successful";
    public static final String TOKEN_REFRESH_SUCCESSFUL = "Token refreshed successfully";
    public static final String TOKENS_REFRESH_SUCCESSFUL = "Tokens refreshed successfully";
    public static final String PASSWORD_RESET_OTP_SENT = "Password reset OTP sent successfully";
    public static final String PASSWORD_RESET_SUCCESSFUL = "Password reset successful";

    // Message Keys - Error Messages
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ACCOUNT_DISABLED = "Account is disabled";
    public static final String ACCOUNT_LOCKED = "Account is locked";
    public static final String ACCOUNT_EXPIRED = "Account has expired";
    public static final String CREDENTIALS_EXPIRED = "Credentials have expired";
    public static final String RATE_LIMIT_EXCEEDED = "Rate limit exceeded. Please try again later.";
    public static final String MAX_TOKENS_EXCEEDED = "Maximum tokens per user exceeded. Please logout from other sessions.";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

    // Error Codes
    public static final String AUTH_ERROR_CODE = "AUTH_ERROR";
    public static final String LOGIN_ERROR_CODE = "LOGIN_FAILED";
    public static final String REGISTER_ERROR_CODE = "REGISTER_FAILED";
    public static final String RESET_PASSWORD_ERROR_CODE = "RESET_PASSWORD_FAILED";

    // JWT Related Constants
    public static final String JWT_SECRET_KEY = "jwt.secret.key";
    public static final String JWT_EXPIRATION_TIME = "jwt.expiration.time";
    public static final String JWT_REFRESH_EXPIRATION_TIME = "jwt.refresh.expiration.time";

    // Session Constants
    public static final String SESSION_ATTRIBUTE_USER = "authenticatedUser";
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long ACCOUNT_LOCK_DURATION_MINUTES = 30;

    // OTP Constants
    public static final int OTP_LENGTH = 6;
    public static final long OTP_EXPIRATION_MINUTES = 10;
    public static final int MAX_OTP_ATTEMPTS = 3;

    // Password Policy
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final boolean REQUIRE_SPECIAL_CHARACTER = true;
    public static final boolean REQUIRE_NUMBER = true;
    public static final boolean REQUIRE_UPPERCASE = true;

    // Security Headers
    public static final String X_AUTH_TOKEN = "X-Auth-Token";
    public static final String X_REFRESH_TOKEN = "X-Refresh-Token";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private AuthConstants() {
        // Utility class
    }
}
