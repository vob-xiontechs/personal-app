<?php

namespace App\Utils;

use Illuminate\Support\Str;
use Illuminate\Support\Facades\Log;

class JwtUtils
{
    /**
     * Extract Bearer token from Authorization header
     */
    public static function extractBearerToken(string $authorizationHeader): ?string
    {
        if (preg_match('/Bearer\s+(.*)$/i', $authorizationHeader, $matches)) {
            return $matches[1];
        }

        return null;
    }

    /**
     * Check if token format is valid (basic JWT structure)
     */
    public static function isValidJwtFormat(string $token): bool
    {
        // JWT format: header.payload.signature
        $parts = explode('.', $token);

        if (count($parts) !== 3) {
            return false;
        }

        // Check if all parts are base64url encoded
        foreach ($parts as $part) {
            if (!self::isValidBase64Url($part)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if string is valid base64url
     */
    private static function isValidBase64Url(string $string): bool
    {
        // Base64url alphabet: A-Z, a-z, 0-9, -, _
        return preg_match('/^[A-Za-z0-9\-_]+$/', $string) === 1;
    }

    /**
     * Decode JWT payload without verification
     */
    public static function decodePayload(string $token): ?array
    {
        try {
            $parts = explode('.', $token);

            if (count($parts) !== 3) {
                return null;
            }

            $payload = json_decode(
                self::base64UrlDecode($parts[1]),
                true
            );

            return $payload;
        } catch (\Exception $e) {
            Log::warning('Failed to decode JWT payload', [
                'error' => $e->getMessage()
            ]);
            return null;
        }
    }

    /**
     * Base64URL decode
     */
    private static function base64UrlDecode(string $data): string
    {
        // Convert base64url to base64
        $data = strtr($data, '-_', '+/');

        // Add padding if needed
        $padding = strlen($data) % 4;
        if ($padding) {
            $data .= str_repeat('=', 4 - $padding);
        }

        return base64_decode($data);
    }

    /**
     * Get token expiration time from payload
     */
    public static function getExpirationTime(array $payload): ?int
    {
        return $payload['exp'] ?? null;
    }

    /**
     * Get token issued at time from payload
     */
    public static function getIssuedAtTime(array $payload): ?int
    {
        return $payload['iat'] ?? null;
    }

    /**
     * Get token issuer from payload
     */
    public static function getIssuer(array $payload): ?string
    {
        return $payload['iss'] ?? null;
    }

    /**
     * Get token subject (user ID) from payload
     */
    public static function getSubject(array $payload): ?string
    {
        return $payload['sub'] ?? null;
    }

    /**
     * Get custom claims from payload
     */
    public static function getCustomClaims(array $payload): array
    {
        $standardClaims = ['iss', 'iat', 'exp', 'nbf', 'sub', 'jti', 'aud'];

        return array_diff_key($payload, array_flip($standardClaims));
    }

    /**
     * Calculate token remaining time in seconds
     */
    public static function getRemainingTime(array $payload): int
    {
        $exp = self::getExpirationTime($payload);

        if (!$exp) {
            return 0;
        }

        return max(0, $exp - time());
    }

    /**
     * Check if token is about to expire (within specified minutes)
     */
    public static function isExpiringSoon(array $payload, int $minutes = 5): bool
    {
        $remainingSeconds = self::getRemainingTime($payload);
        $thresholdSeconds = $minutes * 60;

        return $remainingSeconds <= $thresholdSeconds;
    }

    /**
     * Format token expiration time for display
     */
    public static function formatExpirationTime(array $payload): string
    {
        $exp = self::getExpirationTime($payload);

        if (!$exp) {
            return 'Unknown';
        }

        return date('Y-m-d H:i:s', $exp);
    }

    /**
     * Generate secure random string for JWT ID
     */
    public static function generateJwtId(): string
    {
        return Str::random(32);
    }

    /**
     * Validate JWT ID format
     */
    public static function isValidJwtId(string $jti): bool
    {
        return is_string($jti) && strlen($jti) >= 16;
    }

    /**
     * Get algorithm from JWT header
     */
    public static function getAlgorithm(string $token): ?string
    {
        try {
            $parts = explode('.', $token);

            if (count($parts) !== 3) {
                return null;
            }

            $header = json_decode(
                self::base64UrlDecode($parts[0]),
                true
            );

            return $header['alg'] ?? null;
        } catch (\Exception $e) {
            return null;
        }
    }

    /**
     * Get token type from JWT header
     */
    public static function getTokenType(string $token): ?string
    {
        try {
            $parts = explode('.', $token);

            if (count($parts) !== 3) {
                return null;
            }

            $header = json_decode(
                self::base64UrlDecode($parts[0]),
                true
            );

            return $header['typ'] ?? null;
        } catch (\Exception $e) {
            return null;
        }
    }

    /**
     * Check if token is JWT type
     */
    public static function isJwtToken(string $token): bool
    {
        $type = self::getTokenType($token);
        return $type === 'JWT';
    }

    /**
     * Get token size in bytes
     */
    public static function getTokenSize(string $token): int
    {
        return strlen($token);
    }

    /**
     * Check if token size is within acceptable limits
     */
    public static function isValidTokenSize(string $token, int $maxSize = 4096): bool
    {
        return self::getTokenSize($token) <= $maxSize;
    }
}
