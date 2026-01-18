<?php

namespace App\Services\Auth\Jwt;

use App\Utils\JwtUtils;
use Illuminate\Support\Facades\Facade;
use Tymon\JWTAuth\Facades\JWTAuth;

/**
 * JWT Facade for convenient JWT operations
 *
 * @method static string generateToken(\App\Models\User $user)
 * @method static array|null validateToken(string $token)
 * @method static string refreshToken()
 * @method static bool invalidateToken(?string $token = null)
 * @method static \App\Models\User|null getAuthenticatedUser(?string $token = null)
 * @method static bool isTokenExpired(array $payload)
 * @method static int getTokenExpirationTime()
 * @method static int getTokenTtl()
 * @method static array createTokenResponse(string $token)
 * @method static string|null extractTokenFromRequest(\Illuminate\Http\Request $request)
 * @method static bool verifyTokenSignature(string $token)
 * @method static array|null getTokenClaims(?string $token = null)
 * @method static bool isTokenBlacklisted(string $token)
 */
class JwtFacade extends Facade
{
    /**
     * Get the registered name of the component.
     */
    protected static function getFacadeAccessor(): string
    {
        return 'jwt.facade';
    }

    /**
     * Generate JWT token for user
     */
    public static function generateToken($user): string
    {
        try {
            return JWTAuth::fromUser($user);
        } catch (\Exception $e) {
            throw $e;
        }
    }

    /**
     * Validate JWT token
     */
    public static function validateToken(string $token): ?array
    {
        try {
            $payload = JWTAuth::setToken($token)->getPayload();
            return $payload ? $payload->toArray() : null;
        } catch (\Exception $e) {
            return null;
        }
    }

    /**
     * Refresh JWT token
     */
    public static function refreshToken(): string
    {
        return JWTAuth::refresh();
    }

    /**
     * Invalidate JWT token
     */
    public static function invalidateToken(?string $token = null): bool
    {
        try {
            if ($token) {
                JWTAuth::setToken($token)->invalidate();
            } else {
                JWTAuth::invalidate();
            }
            return true;
        } catch (\Exception $e) {
            return false;
        }
    }

    /**
     * Get authenticated user
     */
    public static function getAuthenticatedUser(?string $token = null): ?\App\Models\User
    {
        try {
            if ($token) {
                return JWTAuth::setToken($token)->authenticate();
            }
            return JWTAuth::parseToken()->authenticate();
        } catch (\Exception $e) {
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    public static function isTokenExpired(array $payload): bool
    {
        return isset($payload['exp']) && $payload['exp'] < time();
    }

    /**
     * Get token expiration time
     */
    public static function getTokenExpirationTime(): int
    {
        return time() + (config('jwt.ttl') * 60);
    }

    /**
     * Get token TTL
     */
    public static function getTokenTtl(): int
    {
        return config('jwt.ttl', 60);
    }

    /**
     * Create token response
     */
    public static function createTokenResponse(string $token): array
    {
        return [
            'token' => $token,
            'token_type' => 'bearer',
            'expires_in' => self::getTokenTtl() * 60,
            'expires_at' => date('c', self::getTokenExpirationTime())
        ];
    }

    /**
     * Extract token from request
     */
    public static function extractTokenFromRequest(\Illuminate\Http\Request $request): ?string
    {
        return JwtUtils::extractBearerToken($request->header('Authorization'))
            ?? $request->input('token');
    }

    /**
     * Verify token signature
     */
    public static function verifyTokenSignature(string $token): bool
    {
        try {
            JWTAuth::setToken($token)->check();
            return true;
        } catch (\Exception $e) {
            return false;
        }
    }

    /**
     * Get token claims
     */
    public static function getTokenClaims(?string $token = null): ?array
    {
        try {
            if ($token) {
                return JWTAuth::setToken($token)->getPayload()->toArray();
            }
            return JWTAuth::getPayload()->toArray();
        } catch (\Exception $e) {
            return null;
        }
    }

    /**
     * Check if token is blacklisted
     */
    public static function isTokenBlacklisted(string $token): bool
    {
        // Placeholder - implement blacklist checking if needed
        return false;
    }
}
