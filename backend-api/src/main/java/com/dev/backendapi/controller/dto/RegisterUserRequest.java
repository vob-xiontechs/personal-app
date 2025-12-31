package com.dev.backendapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enhanced registration request DTO with comprehensive validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    @Schema(description = "User's full name", example = "John Doe", minLength = 2, maxLength = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "User's email address", example = "john.doe@example.com", format = "email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    @Schema(
        description = "User's password",
        example = "SecurePass123!",
        minLength = 8,
        maxLength = 128,
        format = "password"
    )
    private String password;

    // Business logic validation methods
    public boolean isValidEmailDomain() {
        if (email == null) return false;
        String domain = email.substring(email.indexOf('@') + 1);
        // Add business rules for allowed domains
        return !domain.equalsIgnoreCase("temporary.com");
    }

    public boolean isStrongPassword() {
        return password != null &&
               password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[@$!%*?&].*");
    }

    public String getSanitizedName() {
        return name != null ? name.trim().replaceAll("\\s+", " ") : null;
    }

    public String getNormalizedEmail() {
        return email != null ? email.toLowerCase().trim() : null;
    }
}
