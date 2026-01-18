package com.dev.backendapi.utils;

import java.util.Map;

/**
 * Interface for JWT token management operations
 * Follows Interface Segregation Principle
 */
public interface IJwtTokenManager {

    /**
     * Blacklist a token (logout functionality)
     */
    void blacklistToken(String token);

    /**
     * Check if token is blacklisted
     */
    Boolean isTokenBlacklisted(String token);

    /**
     * Clean up expired tokens
     */
    void cleanupExpiredTokens();

    /**
     * Get user session information
     */
    Map<String, Object> getUserSessionInfo(String userId);

    /**
     * Force logout all user tokens
     */
    void forceLogoutUser(String userId);

    /**
     * Get security metrics
     */
    Map<String, Object> getSecurityMetrics();

    /**
     * Register token for user tracking
     */
    void registerUserToken(String userId, String token);

    /**
     * Unregister token from user tracking
     */
    void unregisterUserToken(String userId, String token);

    /**
     * Clear cache for memory management
     */
    void clearCache();
}
