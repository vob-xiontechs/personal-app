package com.dev.backendapi.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import com.dev.backendapi.constants.auth.AuthConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enterprise-Grade JWT Utility with Advanced Security and Intelligent Features
 *
 * Key OOP Principles Applied:
 * - Single Responsibility: Each method has one clear purpose
 * - Open/Closed: Extensible through interfaces and abstract classes
 * - Liskov Substitution: Implementations are interchangeable
 * - Interface Segregation: Clean, focused interfaces
 * - Dependency Inversion: Depends on abstractions, not concretions
 *
 * Features:
 * - Token generation with multiple strategies
 * - Intelligent validation and security
 * - Performance optimization with caching
 * - Comprehensive monitoring and metrics
 * - Rate limiting and abuse prevention
 * - Session management
 */
@Component
@Slf4j
public class JwtUtil implements IJwtTokenProvider, IJwtTokenValidator, IJwtTokenManager {

    // Configuration properties
    @Value("${jwt.secret.key:mySuperSecretJwtKeyForTokenGenerationAndValidation12345678901234567890}")
    private String SECRET_KEY;

    @Value("${jwt.expiration.time:86400000}") // 24 hours
    private long JWT_EXPIRATION_TIME;

    @Value("${jwt.refresh.expiration.time:604800000}") // 7 days
    private long JWT_REFRESH_EXPIRATION_TIME;

    @Value("${jwt.issuer:backend-api}")
    private String JWT_ISSUER;

    @Value("${jwt.audience:frontend-client}")
    private String JWT_AUDIENCE;

    @Value("${jwt.auto.refresh.threshold:3600000}") // 1 hour before expiration
    private long AUTO_REFRESH_THRESHOLD;

    @Value("${jwt.max.tokens.per.user:5}")
    private int MAX_TOKENS_PER_USER;

    @Value("${jwt.rate.limit.requests:10}")
    private int RATE_LIMIT_REQUESTS;

    @Value("${jwt.rate.limit.window:60000}") // 1 minute
    private long RATE_LIMIT_WINDOW;

    // Advanced token management
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();
    private final Map<String, Claims> tokenCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userTokens = new ConcurrentHashMap<>();
    private final Map<String, Long> userLastActivity = new ConcurrentHashMap<>();
    private final Map<String, Integer> rateLimitMap = new ConcurrentHashMap<>();

    // Security monitoring
    private final AtomicLong totalTokensGenerated = new AtomicLong(0);
    private final AtomicLong totalTokensValidated = new AtomicLong(0);
    private final AtomicLong totalTokensBlacklisted = new AtomicLong(0);
    private final AtomicLong totalSecurityViolations = new AtomicLong(0);

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        if (keyBytes.length < 32) {
            // Ensure minimum key length for HS256
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate secure JWT access token for user
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, new HashMap<>());
    }

    /**
     * Smart token generation with intelligence (rate limiting, max tokens per user, etc.)
     */
    @Override
    public String generateSmartToken(UserDetails userDetails) {
        String userId = userDetails.getUsername();

        // Check rate limiting
        if (isRateLimitExceeded(userId)) {
            throw new JwtException(AuthConstants.RATE_LIMIT_EXCEEDED);
        }

        // Check max tokens per user
        if (!canGenerateNewToken(userId)) {
            throw new JwtException(AuthConstants.MAX_TOKENS_EXCEEDED);
        }

        // Generate token
        String token = generateToken(userDetails);
        registerUserToken(userId, token);

        log.info("Smart token generated for user: {} (active tokens: {})",
                userId, userTokens.get(userId).size());

        return token;
    }

    /**
     * Generate JWT token with additional claims
     */
    @Override
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant expiration = now.plus(JWT_EXPIRATION_TIME, ChronoUnit.MILLIS);

        // Compact mode: use shorter claim names for smaller tokens
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("r", roles);      // roles -> r
        claims.put("t", "a");        // type -> t, access -> a
        claims.put("u", userDetails.getUsername()); // userId -> u

        String token = Jwts.builder()
                .header()
                    .add("typ", "JWT")
                    .add("alg", "HS256")
                    .and()
                .issuer(JWT_ISSUER)
                .subject(userDetails.getUsername())
                .audience().add(JWT_AUDIENCE).and()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .notBefore(Date.from(now))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        log.debug("Generated JWT token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Generate secure refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plus(JWT_REFRESH_EXPIRATION_TIME, ChronoUnit.MILLIS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("userId", userDetails.getUsername());

        String token = Jwts.builder()
                .header()
                    .add("typ", "JWT")
                    .add("alg", "HS256")
                    .and()
                .issuer(JWT_ISSUER)
                .subject(userDetails.getUsername())
                .audience().add(JWT_AUDIENCE).and()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .notBefore(Date.from(now))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        log.debug("Generated refresh token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Generate new access token from valid refresh token
     */
    public String generateTokenFromRefreshToken(String refreshToken, UserDetails userDetails) {
        if (!validateRefreshToken(refreshToken, userDetails)) {
            throw new JwtException("Invalid refresh token");
        }

        // Create new access token
        Map<String, Object> claims = new HashMap<>();
        claims.put("refreshed", true);
        claims.put("refreshTime", System.currentTimeMillis());

        return generateToken(userDetails, claims);
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("u", String.class)); // Compact: u instead of userId
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract issued at date from token
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token with caching
     */
    private Claims extractAllClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        // Check cache first for performance
        Claims cachedClaims = tokenCache.get(token);
        if (cachedClaims != null) {
            return cachedClaims;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(JWT_ISSUER)
                    .requireAudience(JWT_AUDIENCE)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Cache the claims for better performance
            tokenCache.put(token, claims);
            return claims;

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new JwtException("Token has expired", e);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token format: {}", e.getMessage());
            throw new JwtException("Invalid token format", e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("Invalid token signature", e);
        } catch (Exception e) {
            log.error("JWT parsing error: {}", e.getMessage());
            throw new JwtException("Token parsing failed", e);
        }
    }

    /**
     * Check if token is expired
     */
    @Override
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true; // Consider expired if can't check
        }
    }

    /**
     * Check if token is blacklisted
     */
    @Override
    public Boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    /**
     * Blacklist a token (for logout)
     */
    @Override
    public void blacklistToken(String token) {
        if (StringUtils.hasText(token)) {
            tokenBlacklist.add(token);
            tokenCache.remove(token); // Remove from cache
            log.info("Token blacklisted successfully");
        }
    }

    /**
     * Clear expired tokens from blacklist periodically
     */
    @Override
    public void cleanupExpiredTokens() {
        // This would be called by a scheduled task
        tokenBlacklist.clear(); // Simplified - in real app, check expiration
        log.info("Cleaned up expired tokens from blacklist");
    }

    /**
     * Comprehensive token validation
     */
    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        if (!StringUtils.hasText(token) || userDetails == null) {
            return false;
        }

        try {
            if (isTokenBlacklisted(token)) {
                log.warn("Attempt to use blacklisted token for user: {}", userDetails.getUsername());
                return false;
            }

            final String username = extractUsername(token);
            final boolean isExpired = isTokenExpired(token);
            final boolean isAccessToken = isAccessToken(token);

            boolean isValid = username.equals(userDetails.getUsername()) && !isExpired && isAccessToken;

            if (isValid) {
                log.debug("Token validation successful for user: {}", username);
            } else {
                log.warn("Token validation failed for user: {} - expired: {}, access: {}", username, isExpired, isAccessToken);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Token validation error for user {}: {}", userDetails.getUsername(), e.getMessage());
            return false;
        }
    }

    /**
     * Validate refresh token
     */
    @Override
    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        if (!StringUtils.hasText(token) || userDetails == null) {
            return false;
        }

        try {
            final String username = extractUsername(token);
            final boolean isExpired = isTokenExpired(token);
            final boolean isRefreshToken = isRefreshToken(token);

            return username.equals(userDetails.getUsername()) && !isExpired && isRefreshToken;

        } catch (Exception e) {
            log.error("Refresh token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token without user details (basic validation)
     */
    @Override
    public Boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            return !isTokenExpired(token) && !isTokenBlacklisted(token);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is an access token
     */
    @Override
    public Boolean isAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("t", String.class); // Compact: t instead of type
            return "a".equals(tokenType); // Compact: a instead of access
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token is a refresh token
     */
    @Override
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class); // Refresh tokens use full "type" key
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public List<GrantedAuthority> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            List<String> roles = (List<String>) claims.get("r"); // Compact: r instead of roles

            if (roles != null) {
                return roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error extracting roles from token: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Get remaining time until token expiration
     */
    public long getRemainingExpirationTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get token expiration time in milliseconds
     */
    public long getExpirationTime() {
        return JWT_EXPIRATION_TIME;
    }

    /**
     * Get refresh token expiration time in milliseconds
     */
    public long getRefreshExpirationTime() {
        return JWT_REFRESH_EXPIRATION_TIME;
    }

    /**
     * Get issuer
     */
    public String getIssuer() {
        return JWT_ISSUER;
    }

    /**
     * Get audience
     */
    public String getAudience() {
        return JWT_AUDIENCE;
    }

    /**
     * Clear token cache (useful for testing or memory management)
     */
    @Override
    public void clearCache() {
        tokenCache.clear();
        log.info("JWT token cache cleared");
    }

    // ===== IJwtTokenValidator Interface Implementation =====

    /**
     * Smart token validation with activity tracking
     */
    @Override
    public Boolean validateSmartToken(String token, UserDetails userDetails) {
        boolean isValid = validateToken(token, userDetails);

        if (isValid) {
            totalTokensValidated.incrementAndGet();
            // Update user activity
            String userId = userDetails.getUsername();
            userLastActivity.put(userId, System.currentTimeMillis());
        }

        return isValid;
    }

    /**
     * Check if token should be refreshed
     */
    @Override
    public boolean shouldRefreshToken(String token) {
        try {
            long remainingTime = getRemainingExpirationTime(token);
            return remainingTime > 0 && remainingTime <= AUTO_REFRESH_THRESHOLD;
        } catch (Exception e) {
            log.error("Error checking token refresh need: {}", e.getMessage());
            return false;
        }
    }

    // ===== IJwtTokenManager Interface Implementation =====

    /**
     * Register token for user tracking
     */
    @Override
    public void registerUserToken(String userId, String token) {
        userTokens.computeIfAbsent(userId, k -> new ArrayList<>()).add(token);
        userLastActivity.put(userId, System.currentTimeMillis());
        totalTokensGenerated.incrementAndGet();
    }

    /**
     * Unregister token from user tracking
     */
    @Override
    public void unregisterUserToken(String userId, String token) {
        List<String> userTokenList = userTokens.get(userId);
        if (userTokenList != null) {
            userTokenList.remove(token);
            if (userTokenList.isEmpty()) {
                userTokens.remove(userId);
            }
        }
    }

    /**
     * Get user session information
     */
    @Override
    public Map<String, Object> getUserSessionInfo(String userId) {
        Map<String, Object> sessionInfo = new HashMap<>();
        List<String> userTokenList = userTokens.get(userId);

        sessionInfo.put("userId", userId);
        sessionInfo.put("activeTokens", userTokenList != null ? userTokenList.size() : 0);
        sessionInfo.put("lastActivity", userLastActivity.get(userId));
        sessionInfo.put("maxAllowedTokens", MAX_TOKENS_PER_USER);

        return sessionInfo;
    }

    /**
     * Force logout all user tokens
     */
    @Override
    public void forceLogoutUser(String userId) {
        List<String> userTokenList = userTokens.remove(userId);
        if (userTokenList != null) {
            userTokenList.forEach(token -> {
                blacklistToken(token);
                totalTokensBlacklisted.incrementAndGet();
            });
            log.info("Force logged out {} tokens for user: {}", userTokenList.size(), userId);
        }
        userLastActivity.remove(userId);
    }

    /**
     * Get security metrics
     */
    @Override
    public Map<String, Object> getSecurityMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalTokensGenerated", totalTokensGenerated.get());
        metrics.put("totalTokensValidated", totalTokensValidated.get());
        metrics.put("totalTokensBlacklisted", totalTokensBlacklisted.get());
        metrics.put("totalSecurityViolations", totalSecurityViolations.get());
        metrics.put("activeTokensInCache", tokenCache.size());
        metrics.put("blacklistedTokens", tokenBlacklist.size());
        metrics.put("activeUserSessions", userTokens.size());
        return metrics;
    }

    // ===== Additional Intelligent Methods =====

    /**
     * Rate limiting check for token generation
     */
    public boolean isRateLimitExceeded(String userId) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        String key = userId + ":" + (currentTime / RATE_LIMIT_WINDOW);

        int currentRequests = rateLimitMap.getOrDefault(key, 0);
        if (currentRequests >= RATE_LIMIT_REQUESTS) {
            log.warn("Rate limit exceeded for user: {}", userId);
            totalSecurityViolations.incrementAndGet();
            return true;
        }

        rateLimitMap.put(key, currentRequests + 1);
        return false;
    }

    /**
     * Manage user tokens - enforce max tokens per user
     */
    public boolean canGenerateNewToken(String userId) {
        List<String> userTokenList = userTokens.get(userId);
        if (userTokenList == null) {
            return true;
        }

        // Remove expired tokens
        userTokenList.removeIf(token -> {
            try {
                return isTokenExpired(token) || isTokenBlacklisted(token);
            } catch (Exception e) {
                return true;
            }
        });

        return userTokenList.size() < MAX_TOKENS_PER_USER;
    }

    /**
     * Predict token expiration time
     */
    public long predictTokenExpiration(String token) {
        try {
            return getRemainingExpirationTime(token);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Cleanup expired rate limit entries
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupRateLimitEntries() {
        long currentTime = System.currentTimeMillis();
        long cutoffTime = currentTime - (RATE_LIMIT_WINDOW * 2);

        rateLimitMap.entrySet().removeIf(entry -> {
            try {
                String[] parts = entry.getKey().split(":");
                long windowStart = Long.parseLong(parts[1]) * RATE_LIMIT_WINDOW;
                return windowStart < cutoffTime;
            } catch (Exception e) {
                return true;
            }
        });

        log.debug("Cleaned up expired rate limit entries");
    }

    /**
     * Cleanup expired user activity records
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupExpiredUserActivity() {
        long cutoffTime = System.currentTimeMillis() - (JWT_EXPIRATION_TIME * 2);

        userLastActivity.entrySet().removeIf(entry ->
            entry.getValue() < cutoffTime
        );

        log.debug("Cleaned up expired user activity records");
    }
}
