package com.dev.backendapi.controller.service;

import com.dev.backendapi.controller.dto.RegisterUserRequest;
import com.dev.backendapi.io.profile.ProfileRequest;
import com.dev.backendapi.io.profile.ProfileResponse;
import com.dev.backendapi.service.profile.ProfileService;
import com.dev.backendapi.service.profile.ProfileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Controller service layer
 * Handles business logic specific to HTTP controllers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ControllerService {

    private final ProfileService profileService;
    private final ProfileValidationUtil profileValidationUtil;

    /**
     * Process user registration with enhanced validation and logging
     */
    public ProfileResponse registerUser(RegisterUserRequest request) {
        log.info("Processing user registration for email: {}", profileValidationUtil.normalizeEmail(request.getEmail()));

        // Additional business validation
        validateRegistrationRequest(request);

        // Convert to domain object
        ProfileRequest domainRequest = convertToProfileRequest(request);

        // Call business service
        ProfileResponse response = profileService.createProfile(domainRequest);

        log.info("User registration successful for userId: {}", response.getUserId());

        return response;
    }

    /**
     * Additional validation specific to controller layer
     */
    private void validateRegistrationRequest(RegisterUserRequest request) {
        // Business rules validation
        if (!profileValidationUtil.isValidEmailDomain(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email domain");
        }

        if (!profileValidationUtil.isStrongPassword(request.getPassword())) {
            throw new IllegalArgumentException("Password does not meet security requirements");
        }

        // Additional checks can be added here
        validateNameFormat(profileValidationUtil.sanitizeName(request.getName()));
    }

    /**
     * Validate name format
     */
    private void validateNameFormat(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Check for common invalid patterns
        if (name.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Name cannot contain numbers");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters");
        }
    }

    /**
     * Convert controller DTO to domain object
     */
    private ProfileRequest convertToProfileRequest(RegisterUserRequest request) {
        return new ProfileRequest(
            profileValidationUtil.sanitizeName(request.getName()),
            profileValidationUtil.normalizeEmail(request.getEmail()),
            request.getPassword() // Note: In production, this should be hashed
        );
    }

    /**
     * Sanitize and validate input data
     */
    public RegisterUserRequest sanitizeRequest(RegisterUserRequest request) {
        return RegisterUserRequest.builder()
                .name(profileValidationUtil.sanitizeName(request.getName()))
                .email(profileValidationUtil.normalizeEmail(request.getEmail()))
                .password(request.getPassword()) // Password should be validated but not modified
                .build();
    }
}
