package com.dev.backendapi.controller.dto;

import com.dev.backendapi.constants.profile.ProfileConstants;
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
@Schema(
    description = "User registration request",
    example = """
    {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "SecurePass123!"
    }
    """
)
public class RegisterUserRequest {

    @NotBlank(message = ProfileConstants.NAME_REQUIRED)
    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @NotBlank(message = ProfileConstants.EMAIL_REQUIRED)
    @Email(message = ProfileConstants.INVALID_EMAIL_FORMAT)
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = ProfileConstants.PASSWORD_REQUIRED)
    @Size(min = ProfileConstants.MIN_PASSWORD_LENGTH, message = ProfileConstants.PASSWORD_TOO_SHORT)
    @Schema(description = "User's password", example = "SecurePass123!")
    private String password;


}
