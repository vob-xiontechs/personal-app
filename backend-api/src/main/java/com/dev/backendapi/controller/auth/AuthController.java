package com.dev.backendapi.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.backendapi.controller.dto.ApiResponse;
import com.dev.backendapi.controller.dto.AuthRequest.LoginRequest;
import com.dev.backendapi.controller.dto.AuthRequest.ResetPasswordRequest;
import com.dev.backendapi.controller.dto.AuthRequest.SendResetOtpRequest;
import com.dev.backendapi.controller.dto.AuthResponse;
import com.dev.backendapi.service.auth.AuthService;
import com.dev.backendapi.utils.controller.ControllerUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;
    private final ControllerUtils controllerUtils;

    /**
     * Authenticate user with email and password
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Account disabled"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIpAddress(httpRequest);

        log.info("Login attempt for email: {} from IP: {}", request.getEmail(), clientIp);

        try {
            AuthResponse authResponse = authService.authenticate(request);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Login failed for email: {} from IP: {}", request.getEmail(), clientIp, e);
            throw e;
        }
    }

    /**
     * Send password reset OTP
     */
    @PostMapping("/send-reset-otp")
    public ResponseEntity<ApiResponse<String>> sendResetOtp(@Valid @RequestBody SendResetOtpRequest request) {
        String sanitizedEmail = controllerUtils.sanitizeEmail(request.getEmail());

        try {
            authService.sendPasswordResetOtp(sanitizedEmail);
            controllerUtils.logOperation("send_reset_otp", sanitizedEmail, true);
            return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
        } catch (UnsupportedOperationException e) {
            controllerUtils.logOperation("send_reset_otp", sanitizedEmail, false);
            return ResponseEntity.status(501)
                .body(ApiResponse.error("NOT_IMPLEMENTED", "Password reset OTP not yet implemented"));
        } catch (Exception e) {
            controllerUtils.logOperation("send_reset_otp", sanitizedEmail, false);
            log.error("Error sending reset OTP for email: {}", sanitizedEmail, e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("SYSTEM_ERROR", "Failed to send reset OTP"));
        }
    }

    /**
     * Reset password with OTP
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String sanitizedEmail = controllerUtils.sanitizeEmail(request.getEmail());

        try {
            authService.resetPassword(sanitizedEmail, request.getOtp(), request.getNewPassword());
            controllerUtils.logOperation("reset_password", sanitizedEmail, true);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
        } catch (UnsupportedOperationException e) {
            controllerUtils.logOperation("reset_password", sanitizedEmail, false);
            return ResponseEntity.status(501)
                .body(ApiResponse.error("NOT_IMPLEMENTED", "Password reset not yet implemented"));
        } catch (Exception e) {
            controllerUtils.logOperation("reset_password", sanitizedEmail, false);
            log.error("Error resetting password for email: {}", sanitizedEmail, e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("SYSTEM_ERROR", "Failed to reset password"));
        }
    }

    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            // Get authenticated user email from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication != null ? authentication.getName() : "unknown";

            authService.logout(userEmail);
            controllerUtils.logOperation("user_logout", userEmail, true);
            return ResponseEntity.ok(ApiResponse.success("Logout successful"));
        } catch (Exception e) {
            // Get authenticated user email from security context for error logging
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication != null ? authentication.getName() : "unknown";

            controllerUtils.logOperation("user_logout", userEmail, false);
            log.error("Error during logout for user: {}", userEmail, e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("SYSTEM_ERROR", "Logout failed"));
        }
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
