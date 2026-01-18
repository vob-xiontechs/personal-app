package com.dev.backendapi.utils;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Interface for JWT token generation operations
 * Follows Interface Segregation Principle
 */
public interface IJwtTokenProvider {

    /**
     * Generate access token for user
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generate token with additional claims
     */
    String generateToken(UserDetails userDetails, Map<String, Object> extraClaims);

    /**
     * Generate refresh token
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Generate new access token from refresh token
     */
    String generateTokenFromRefreshToken(String refreshToken, UserDetails userDetails);

    /**
     * Smart token generation with business logic
     */
    String generateSmartToken(UserDetails userDetails);

    /**
     * Check if token is expired
     */
    Boolean isTokenExpired(String token);

    /**
     * Check if token is blacklisted
     */
    Boolean isTokenBlacklisted(String token);
}
