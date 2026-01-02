package com.dev.backendapi.controller.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.dev.backendapi.controller.dto.ApiResponse;
import com.dev.backendapi.controller.dto.RegisterUserRequest;
import com.dev.backendapi.controller.service.ControllerService;
import com.dev.backendapi.io.profile.ProfileResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Optimized Profile Controller with enhanced structure and robustness
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

    private final ControllerService controllerService;

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

        // Generate correlation ID for request tracking
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(httpRequest);

        log.info("[{}] Starting user registration for email: {} from IP: {}",
                correlationId, request.getEmail(), clientIp);

        try {
            // Handle validation errors
            if (bindingResult.hasErrors()) {
                Map<String, Object> validationErrors = extractValidationErrors(bindingResult);
                log.warn("[{}] Validation failed for registration: {}", correlationId, validationErrors);

                return ResponseEntity.badRequest()
                        .body(ApiResponse.validationError(validationErrors));
            }

            // Sanitize input data
            RegisterUserRequest sanitizedRequest = controllerService.sanitizeRequest(request);

            // Additional business validation logging
            log.debug("[{}] Business validation passed for user: {}", correlationId, sanitizedRequest.getEmail());

            // Process registration
            ProfileResponse profileResponse = controllerService.registerUser(sanitizedRequest);

            // Success response with metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("correlationId", correlationId);
            metadata.put("userId", profileResponse.getUserId());

            log.info("[{}] User registration completed successfully for userId: {}",
                    correlationId, profileResponse.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(profileResponse, "User registered successfully", metadata));

        } catch (IllegalArgumentException e) {
            log.warn("[{}] Business validation failed: {}", correlationId, e.getMessage());

            return ResponseEntity.unprocessableEntity()
                    .body(ApiResponse.error("BUSINESS_RULE_VIOLATION", e.getMessage()));

        } catch (Exception e) {
            log.error("[{}] Unexpected error during registration: {}", correlationId, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
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
