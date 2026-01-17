package com.dev.backendapi.controller.service;

import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.dev.backendapi.service.profile.ProfileValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller Utilities Service
 *
 * Provides reusable utility methods for controllers.
 * Following standard Spring Boot architecture patterns.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ControllerService {

    private final ProfileValidationUtil profileValidationUtil;

    // ==================== VALIDATION UTILITIES ====================

    /**
     * Generic validation method
     */
    public <T> void validate(T input, Predicate<T> validator, String errorMessage) {
        if (!validator.test(input)) {
            log.warn("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // ==================== DATA SANITIZATION UTILITIES ====================

    /**
     * Sanitize string with null safety
     */
    public String sanitizeString(String input, Function<String, String> sanitizer) {
        return input != null ? sanitizer.apply(input) : null;
    }

    /**
     * Sanitize email
     */
    public String sanitizeEmail(String email) {
        return sanitizeString(email, profileValidationUtil::normalizeEmail);
    }

    /**
     * Sanitize name
     */
    public String sanitizeName(String name) {
        return sanitizeString(name, profileValidationUtil::sanitizeName);
    }

    // ==================== LOGGING UTILITIES ====================

    /**
     * Log operation result
     */
    public void logOperation(String operation, String identifier, boolean success) {
        if (success) {
            log.info("{} completed successfully for: {}", operation, identifier);
        } else {
            log.error("{} failed for: {}", operation, identifier);
        }
    }
}
