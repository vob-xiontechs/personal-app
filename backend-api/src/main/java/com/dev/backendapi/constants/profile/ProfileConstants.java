package com.dev.backendapi.constants.profile;

/**
 * Constants class for profile-related configurations and validation rules
 */
public final class ProfileConstants {

    // Password validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int STRONG_PASSWORD_MIN_LENGTH = 8;
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
    public static final String STRONG_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";

    // Name validation constants
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 2;
    public static final String NAME_PATTERN = "^[a-zA-Z\\s]+$";

    // Email validation constants
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String BLOCKED_EMAIL_DOMAIN = "temporary.com";

    // OTP and verification constants
    public static final long DEFAULT_OTP_EXPIRE_TIME = 0L;
    public static final int OTP_LENGTH = 6;

    // Database table names
    public static final String USER_TABLE_NAME = "tbl_users";

    // Validation messages
    public static final String NAME_REQUIRED = "Name is required";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String NAME_TOO_LONG = "Name must not exceed " + MAX_NAME_LENGTH + " characters";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long";
    public static final String PASSWORD_WEAK = "Password must contain at least one uppercase letter, one lowercase letter, and one digit";
    public static final String EMAIL_TOO_LONG = "Email must not exceed " + MAX_EMAIL_LENGTH + " characters";
    public static final String INVALID_NAME_FORMAT = "Name can only contain letters and spaces";

    // Private constructor to prevent instantiation
    private ProfileConstants() {
        throw new UnsupportedOperationException("ProfileConstants is a utility class and cannot be instantiated");
    }
}
