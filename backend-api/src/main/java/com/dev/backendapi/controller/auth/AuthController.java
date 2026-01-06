package com.dev.backendapi.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.backendapi.controller.dto.ApiResponse;
import com.dev.backendapi.controller.dto.AuthRequest.LoginRequest;
import com.dev.backendapi.controller.dto.AuthRequest.ResetPasswordRequest;
import com.dev.backendapi.controller.dto.AuthRequest.SendResetOtpRequest;
import com.dev.backendapi.controller.dto.AuthResponse;
import com.dev.backendapi.controller.service.AuthControllerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthControllerService authControllerService;

    /**
     * User login endpoint with comprehensive validation and error handling
     *
     * @param request the login request containing email and password
     * @param httpRequest the HTTP servlet request for extracting client IP
     * @return ResponseEntity containing ApiResponse with AuthResponse on success,
     *         or error response on authentication failure
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "User Login",
        description = "Authenticate user with email and password, returns JWT tokens and sets secure cookie"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(
                    implementation = com.dev.backendapi.controller.dto.AuthResponse.class
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Account disabled",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "423",
            description = "Account locked",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @io.swagger.v3.oas.annotations.media.Content
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        return authControllerService.login(request, httpRequest);
    }



    /**
     * Send password reset OTP
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Send Password Reset OTP",
        description = "Send OTP to user's email for password reset"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "OTP sent successfully",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "501",
            description = "Feature not implemented",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @io.swagger.v3.oas.annotations.media.Content
        )
    })
    @PostMapping("/send-reset-otp")
    public ResponseEntity<ApiResponse<String>> sendResetOtp(@Valid @RequestBody SendResetOtpRequest request) {
        return authControllerService.sendResetOtp(request.getEmail());
    }

    /**
     * Reset password with OTP
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Reset Password with OTP",
        description = "Reset user password using OTP verification"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Password reset successfully",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "501",
            description = "Feature not implemented",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @io.swagger.v3.oas.annotations.media.Content
        )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authControllerService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
    }

    /**
     * User logout endpoint
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "User Logout",
        description = "Logout user and invalidate session"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @io.swagger.v3.oas.annotations.media.Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @io.swagger.v3.oas.annotations.media.Content
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // TODO: Get user email from security context
        return authControllerService.logout("user@example.com");
    }

}
