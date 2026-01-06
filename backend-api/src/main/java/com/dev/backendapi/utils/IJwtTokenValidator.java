package com.dev.backendapi.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Interface for JWT token validation operations
 * Follows Interface Segregation Principle
 */
public interface IJwtTokenValidator {

    /**
     * Validate token against user details
     */
    Boolean validateToken(String token, UserDetails userDetails);

    /**
     * Validate refresh token
     */
    Boolean validateRefreshToken(String token, UserDetails userDetails);

    /**
     * Basic token validation
     */
    Boolean validateToken(String token);

    /**
     * Smart token validation with business logic
     */
    Boolean validateSmartToken(String token, UserDetails userDetails);

    /**
     * Check if token is expired
     */
    Boolean isTokenExpired(String token);

    /**
     * Check if token is blacklisted
     */
    Boolean isTokenBlacklisted(String token);

    /**
     * Check if token is access token
     */
    Boolean isAccessToken(String token);

    /**
     * Check if token is refresh token
     */
    Boolean isRefreshToken(String token);

    /**
     * Check if token should be refreshed
     */
    boolean shouldRefreshToken(String token);
}
