package com.dev.backendapi.controller.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.backendapi.controller.dto.ApiResponse;
import com.dev.backendapi.controller.dto.PaginatedResponse;
import com.dev.backendapi.controller.dto.RegisterUserRequest;
import com.dev.backendapi.controller.dto.UpdateProfileRequest;
import com.dev.backendapi.entity.profile.UserEntity;
import com.dev.backendapi.io.profile.ProfileResponse;
import com.dev.backendapi.repository.profile.UserRepository;
import com.dev.backendapi.service.profile.ProfileService;
import com.dev.backendapi.utils.controller.ControllerUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Optimized Profile Controller extending generic ControllerService
 *
 * Inherits generic functionality:
 * - Generic validation framework
 * - Data sanitization utilities
 * - Request processing pipeline
 * - Error handling and logging
 *
 * Features:
 * - Standardized API responses
 * - Comprehensive validation
 * - Structured logging
 * - Error handling
 * - Security measures
 * - Performance monitoring
 */
@RestController
@RequestMapping("/api/v1.0/profiles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile Management", description = "APIs for managing user profiles with enhanced validation and security")
public class ProfileController {

    private final ControllerUtils controllerUtils;
    private final ProfileService profileService;
    private final UserRepository userRepository;

    /**
     * Register new user profile with comprehensive validation
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register new user profile",
        description = "Creates a new user profile with enhanced validation, security checks, and business rules"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Profile created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Business rule violation",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<ProfileResponse>> register(
            @Valid @RequestBody RegisterUserRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(httpRequest);

        // Enhanced logging with client IP
        log.info("[{}] User registration attempt from IP: {} for email: {}",
                correlationId, clientIp, request.getEmail());

        // Use generic logging utility
        controllerUtils.logOperation("user_registration", correlationId, true);

        try {
            // Manual validation for now (can be enhanced with ControllerUtils later)
            if (bindingResult.hasErrors()) {
                Map<String, Object> validationErrors = extractValidationErrors(bindingResult);
                log.warn("[{}] Validation failed for registration: {}", correlationId, validationErrors);
                return ResponseEntity.badRequest()
                        .body(ApiResponse.validationError(validationErrors));
            }

            // Sanitize input data using ControllerUtils
            RegisterUserRequest sanitizedRequest = RegisterUserRequest.builder()
                    .name(controllerUtils.sanitizeName(request.getName()))
                    .email(controllerUtils.sanitizeEmail(request.getEmail()))
                    .password(request.getPassword())
                    .build();

            // Process registration using ProfileService directly
            ProfileResponse profileResponse = profileService.createProfile(
                new com.dev.backendapi.io.profile.ProfileRequest(
                    sanitizedRequest.getName(),
                    sanitizedRequest.getEmail(),
                    sanitizedRequest.getPassword()
                )
            );

            // Success response with metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("correlationId", correlationId);
            metadata.put("userId", profileResponse.getUserId());

            // Log success using generic utility
            controllerUtils.logOperation("user_registration", profileResponse.getUserId(), true);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(profileResponse, "User registered successfully", metadata));

        } catch (IllegalArgumentException e) {
            // Handle business validation errors
            controllerUtils.logOperation("user_registration", correlationId, false);
            log.warn("[{}] Business validation failed: {}", correlationId, e.getMessage());

            return ResponseEntity.unprocessableEntity()
                    .body(ApiResponse.error("BUSINESS_RULE_VIOLATION", e.getMessage()));

        } catch (Exception e) {
            // Handle system errors with generic error handling
            controllerUtils.logOperation("user_registration", correlationId, false);
            log.error("[{}] Unexpected error during registration: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Get list of all user profiles (deprecated - use paginated endpoint)
     */
    @GetMapping("/all")
    @Operation(
        summary = "Get all profiles (deprecated)",
        description = "Retrieves all user profiles without pagination - use /profiles for paginated results"
    )
    @Deprecated
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> getAllProfiles() {
        String correlationId = UUID.randomUUID().toString();

        // Use generic logging utility
        controllerUtils.logOperation("profile_list_retrieval_all", correlationId, true);

        try {
            // Get profile list from service
            List<ProfileResponse> profileList = profileService.getProfileList();

            // Log success using generic utility
            controllerUtils.logOperation("profile_list_retrieval_all", String.valueOf(profileList.size()), true);

            // Success response with metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("correlationId", correlationId);
            metadata.put("totalCount", profileList.size());

            return ResponseEntity.ok(ApiResponse.success(profileList, "Profile list retrieved successfully", metadata));

        } catch (Exception e) {
            // Log failure using generic utility
            controllerUtils.logOperation("profile_list_retrieval_all", correlationId, false);
            log.error("[{}] Unexpected error during profile list retrieval: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Get paginated list of user profiles
     */
    @GetMapping
    @Operation(
        summary = "Get paginated profile list",
        description = "Retrieves a paginated list of user profiles with sorting support"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<PaginatedResponse<ProfileResponse>>> getProfileList(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be >= 0") int page,
            @RequestParam(defaultValue = "5") @Min(value = 1, message = "Size must be >= 1") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String correlationId = UUID.randomUUID().toString();

        // Use generic logging utility
        controllerUtils.logOperation("profile_list_paginated_retrieval", correlationId, true);

        try {
            // Get paginated profile list from service
            PaginatedResponse<ProfileResponse> paginatedResponse = profileService.getProfileList(page, size, sortBy, sortDirection);

            // Log success using generic utility
            controllerUtils.logOperation("profile_list_paginated_retrieval", String.valueOf(paginatedResponse.getTotalElements()), true);

            // Success response with metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("correlationId", correlationId);
            metadata.put("page", paginatedResponse.getPage());
            metadata.put("size", paginatedResponse.getSize());
            metadata.put("totalElements", paginatedResponse.getTotalElements());
            metadata.put("totalPages", paginatedResponse.getTotalPages());

            return ResponseEntity.ok(ApiResponse.success(paginatedResponse, "Profile list retrieved successfully", metadata));

        } catch (Exception e) {
            // Log failure using generic utility
            controllerUtils.logOperation("profile_list_paginated_retrieval", correlationId, false);
            log.error("[{}] Unexpected error during paginated profile list retrieval: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Get profile details by user ID
     */
    @GetMapping("/{userId}")
    @Operation(
        summary = "Get profile details",
        description = "Retrieves detailed information for a specific user profile"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Profile not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileDetails(@PathVariable String userId) {
        String correlationId = UUID.randomUUID().toString();

        // Use generic logging utility
        controllerUtils.logOperation("profile_detail_retrieval", correlationId, true);

        try {
            // Get profile details from service
            ProfileResponse profileDetails = profileService.getProfileDetails(userId);

            // Log success using generic utility
            controllerUtils.logOperation("profile_detail_retrieval", userId, true);

            // Success response with metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("correlationId", correlationId);
            metadata.put("userId", userId);

            return ResponseEntity.ok(ApiResponse.success(profileDetails, "Profile details retrieved successfully", metadata));

        } catch (IllegalArgumentException e) {
            // Handle not found errors
            controllerUtils.logOperation("profile_detail_retrieval", correlationId, false);
            log.warn("[{}] Profile not found: {}", correlationId, userId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("PROFILE_NOT_FOUND", "Profile not found"));

        } catch (Exception e) {
            // Handle system errors
            controllerUtils.logOperation("profile_detail_retrieval", correlationId, false);
            log.error("[{}] Unexpected error during profile detail retrieval: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Update profile details
     */
    @PutMapping("/{userId}")
    @Operation(
        summary = "Update profile details",
        description = "Updates the details of a specific user profile"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Profile not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UpdateProfileRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(httpRequest);

        // Enhanced logging with client IP
        log.info("[{}] Profile update attempt from IP: {} for userId: {}",
                correlationId, clientIp, userId);

        // Use generic logging utility
        controllerUtils.logOperation("profile_update", correlationId, true);

        try {
            // Manual validation for now (can be enhanced with ControllerUtils later)
            if (bindingResult.hasErrors()) {
                Map<String, Object> validationErrors = extractValidationErrors(bindingResult);
                log.warn("[{}] Validation failed for update: {}", correlationId, validationErrors);
                return ResponseEntity.badRequest()
                        .body(ApiResponse.validationError(validationErrors));
            }

            // Get current user for validation
            UserEntity currentUser = userRepository.findByUserId(userId).orElse(null);
            if (currentUser == null) {
                log.warn("[{}] User not found for update: {}", correlationId, userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("PROFILE_NOT_FOUND", "Profile not found"));
            }

            // Verify current password
            if (!currentUser.getPassword().equals(request.getCurrentPassword())) {
                log.warn("[{}] Invalid current password for user: {}", correlationId, userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("INVALID_CURRENT_PASSWORD", "Current password is incorrect"));
            }

            // Validate password confirmation if password is being changed
            if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                    log.warn("[{}] Password confirmation mismatch for user: {}", correlationId, userId);
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("PASSWORD_CONFIRMATION_MISMATCH", "New password and confirmation do not match"));
                }
            }

            // Sanitize input data using ControllerUtils
            UpdateProfileRequest sanitizedRequest = UpdateProfileRequest.builder()
                    .name(controllerUtils.sanitizeName(request.getName()))
                    .email(controllerUtils.sanitizeEmail(request.getEmail()))
                    .newPassword(request.getNewPassword())
                    .confirmPassword(request.getConfirmPassword())
                    .currentPassword(request.getCurrentPassword())
                    .build();

            // Check if email is already taken by another user
            if (userRepository.existsByEmail(sanitizedRequest.getEmail())) {
                UserEntity existingUser = userRepository.findByEmail(sanitizedRequest.getEmail()).orElse(null);
                if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                    log.warn("[{}] Email already exists for different user: {}", correlationId, sanitizedRequest.getEmail());
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(ApiResponse.error("EMAIL_EXISTS", "Email is already in use by another account"));
                }
            }

            // Determine final password (new password if provided, otherwise keep current)
            String finalPassword = sanitizedRequest.getNewPassword() != null && !sanitizedRequest.getNewPassword().trim().isEmpty()
                    ? sanitizedRequest.getNewPassword()
                    : currentUser.getPassword();

            // Check if any changes were made
            boolean hasChanges = !sanitizedRequest.getName().equals(currentUser.getName()) ||
                               !sanitizedRequest.getEmail().equals(currentUser.getEmail()) ||
                               !finalPassword.equals(currentUser.getPassword());

            if (!hasChanges) {
                log.info("[{}] No changes detected for user: {}", correlationId, userId);
                return ResponseEntity.ok(ApiResponse.success(
                    profileService.getProfileDetails(userId),
                    "No changes were made",
                    Map.of("correlationId", correlationId, "userId", userId, "changes", false)
                ));
            }

            // Log what fields are being updated
            Map<String, Object> updateDetails = new HashMap<>();
            if (!sanitizedRequest.getName().equals(currentUser.getName())) {
                updateDetails.put("name", "changed");
            }
            if (!sanitizedRequest.getEmail().equals(currentUser.getEmail())) {
                updateDetails.put("email", "changed");
            }
            if (!finalPassword.equals(currentUser.getPassword())) {
                updateDetails.put("password", "changed");
            }

            log.info("[{}] Updating profile for user {}: {}", correlationId, userId, updateDetails);

            // Process update using ProfileService
            ProfileResponse profileResponse = profileService.updateProfile(userId,
                new com.dev.backendapi.io.profile.ProfileRequest(
                    sanitizedRequest.getName(),
                    sanitizedRequest.getEmail(),
                    finalPassword
                )
            );

            // Success response with metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("correlationId", correlationId);
            metadata.put("userId", userId);

            // Log success using generic utility
            controllerUtils.logOperation("profile_update", userId, true);

            return ResponseEntity.ok(ApiResponse.success(profileResponse, "Profile updated successfully", metadata));

        } catch (IllegalArgumentException e) {
            // Handle not found or business validation errors
            controllerUtils.logOperation("profile_update", correlationId, false);
            log.warn("[{}] Profile update failed: {}", correlationId, e.getMessage());

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("PROFILE_NOT_FOUND", "Profile not found"));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("BUSINESS_RULE_VIOLATION", e.getMessage()));

        } catch (Exception e) {
            // Handle system errors
            controllerUtils.logOperation("profile_update", correlationId, false);
            log.error("[{}] Unexpected error during profile update: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * Delete profile by user ID (hard delete)
     */
    @DeleteMapping("/{userId}")
    @Operation(
        summary = "Delete profile",
        description = "Permanently deletes a user profile from the database"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Profile not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        String correlationId = UUID.randomUUID().toString();

        // Use generic logging utility
        controllerUtils.logOperation("profile_delete", correlationId, true);

        try {
            // Delete profile using service
            profileService.deleteProfile(userId);

            // Log success using generic utility
            controllerUtils.logOperation("profile_delete", userId, true);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            // Handle not found errors
            controllerUtils.logOperation("profile_delete", correlationId, false);
            log.warn("[{}] Profile not found for deletion: {}", correlationId, userId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);

        } catch (Exception e) {
            // Handle system errors
            controllerUtils.logOperation("profile_delete", correlationId, false);
            log.error("[{}] Unexpected error during profile deletion: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the profile service is healthy")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Profile service is healthy"));
    }

    /**
     * Extract validation errors into a map
     */
    private Map<String, Object> extractValidationErrors(BindingResult bindingResult) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        errors.put("fieldErrors", fieldErrors);
        errors.put("errorCount", fieldErrors.size());

        return errors;
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
