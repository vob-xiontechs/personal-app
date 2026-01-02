package com.dev.backendapi.service.profile;

import org.springframework.stereotype.Component;

import com.dev.backendapi.constants.profile.ProfileConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Utility class for profile-related business validation logic
 */
@Component
@Hidden
public final class ProfileValidationUtil {

    /**
     * Check if email domain is valid
     */
    @JsonIgnore
    public boolean isValidEmailDomain(String email) {
        if (email == null) return false;
        String domain = email.substring(email.indexOf('@') + 1);
        // Add business rules for allowed domains
        return !domain.equalsIgnoreCase(ProfileConstants.BLOCKED_EMAIL_DOMAIN);
    }

    /**
     * Check if password meets strength requirements
     */
    @JsonIgnore
    public boolean isStrongPassword(String password) {
        return password != null &&
               password.length() >= ProfileConstants.STRONG_PASSWORD_MIN_LENGTH &&
               password.matches(ProfileConstants.STRONG_PASSWORD_PATTERN);
    }

    /**
     * Sanitize name by trimming and normalizing whitespace
     */
    @JsonIgnore
    public String sanitizeName(String name) {
        return name != null ? name.trim().replaceAll("\\s+", " ") : null;
    }

    /**
     * Normalize email to lowercase and trim
     */
    @JsonIgnore
    public String normalizeEmail(String email) {
        return email != null ? email.toLowerCase().trim() : null;
    }
}
