package com.dev.backendapi.controller.service;

import com.dev.backendapi.controller.dto.RegisterUserRequest;
import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.service.ProfileService;
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

    /**
     * Process user registration with enhanced validation and logging
     */
    public ProfileResponse registerUser(RegisterUserRequest request) {
        log.info("Processing user registration for email: {}", request.getNormalizedEmail());

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
        if (!request.isValidEmailDomain()) {
            throw new IllegalArgumentException("Invalid email domain");
        }

        if (!request.isStrongPassword()) {
            throw new IllegalArgumentException("Password does not meet security requirements");
        }

        // Additional checks can be added here
        validateNameFormat(request.getSanitizedName());
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
            request.getSanitizedName(),
            request.getNormalizedEmail(),
            request.getPassword() // Note: In production, this should be hashed
        );
    }

    /**
     * Sanitize and validate input data
     */
    public RegisterUserRequest sanitizeRequest(RegisterUserRequest request) {
        return RegisterUserRequest.builder()
                .name(request.getSanitizedName())
                .email(request.getNormalizedEmail())
                .password(request.getPassword()) // Password should be validated but not modified
                .build();
    }
}
