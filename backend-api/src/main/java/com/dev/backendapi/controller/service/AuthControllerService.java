package com.dev.backendapi.controller.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import com.dev.backendapi.constants.auth.AuthConstants;
import com.dev.backendapi.controller.dto.ApiResponse;
import com.dev.backendapi.controller.dto.AuthRequest.LoginRequest;
import com.dev.backendapi.controller.dto.AuthResponse;
import com.dev.backendapi.service.auth.AuthService;
import com.dev.backendapi.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller service for authentication operations
 * Handles HTTP-specific logic and response building
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class AuthControllerService {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * Process login request with comprehensive error handling
     *
     * @param request the login request
     * @param httpRequest the HTTP servlet request for IP extraction
     * @return ResponseEntity with authentication result and secure cookie
     */
    public ResponseEntity<AuthResponse> login(LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIpAddress(httpRequest);

        log.info("Login attempt for email: {} from IP: {}", request.getEmail(), clientIp);

        try {
            // Validate request
            authService.validateLoginRequest(request);

            // Authenticate and get response
            AuthResponse authResponse = authService.authenticate(request);

            // Create secure cookie
            ResponseCookie cookie = createJwtCookie(authResponse.getAccessToken());

            log.info("Login successful for user: {} from IP: {}", authResponse.getEmail(), clientIp);

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {} from IP: {}", request.getEmail(), clientIp);
            throw new IllegalArgumentException(AuthConstants.INVALID_CREDENTIALS);

        } catch (DisabledException e) {
            log.warn("Account disabled for email: {} from IP: {}", request.getEmail(), clientIp);
            throw new IllegalStateException(AuthConstants.ACCOUNT_DISABLED);

        } catch (LockedException e) {
            log.warn("Account locked for email: {} from IP: {}", request.getEmail(), clientIp);
            throw new IllegalStateException(AuthConstants.ACCOUNT_LOCKED);

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email: {} from IP: {} - {}", request.getEmail(), clientIp, e.getMessage());
            throw new IllegalArgumentException(AuthConstants.AUTHENTICATION_FAILED);

        } catch (IllegalArgumentException e) {
            log.warn("Validation failed for email: {} from IP: {} - {}", request.getEmail(), clientIp, e.getMessage());
            throw e; // Re-throw validation errors

        } catch (Exception e) {
            log.error("Unexpected error during login for email: {} from IP: {}", request.getEmail(), clientIp, e);
            throw new RuntimeException("An unexpected error occurred");
        }
    }

    /**
     * Process password reset OTP request
     *
     * @param email the email address
     * @return ResponseEntity with result
     */
    public ResponseEntity<ApiResponse<String>> sendResetOtp(String email) {
        try {
            authService.sendPasswordResetOtp(email);
            return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(501)
                .body(ApiResponse.error("NOT_IMPLEMENTED", "Password reset OTP not yet implemented"));
        } catch (Exception e) {
            log.error("Error sending reset OTP for email: {}", email, e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("SYSTEM_ERROR", "Failed to send reset OTP"));
        }
    }

    /**
     * Process password reset request
     *
     * @param email the email address
     * @param otp the OTP
     * @param newPassword the new password
     * @return ResponseEntity with result
     */
    public ResponseEntity<ApiResponse<String>> resetPassword(String email, String otp, String newPassword) {
        try {
            authService.resetPassword(email, otp, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(501)
                .body(ApiResponse.error("NOT_IMPLEMENTED", "Password reset not yet implemented"));
        } catch (Exception e) {
            log.error("Error resetting password for email: {}", email, e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("SYSTEM_ERROR", "Failed to reset password"));
        }
    }

    /**
     * Process logout request
     *
     * @param email the email of the user logging out
     * @return ResponseEntity with result
     */
    public ResponseEntity<ApiResponse<String>> logout(String email) {
        try {
            authService.logout(email);
            return ResponseEntity.ok(ApiResponse.success(AuthConstants.LOGOUT_SUCCESSFUL));
        } catch (Exception e) {
            log.error("Error during logout for email: {}", email, e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error("SYSTEM_ERROR", "Logout failed"));
        }
    }

    /**
     * Create a secure JWT cookie
     */
    private ResponseCookie createJwtCookie(String accessToken) {
        return ResponseCookie.from("jwt", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtUtil.getExpirationTime())
            .build();
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
