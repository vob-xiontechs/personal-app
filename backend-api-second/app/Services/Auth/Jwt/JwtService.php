<?php

namespace App\Services\Auth\Jwt;

use App\Models\User;
use App\Utils\JwtUtils;
use Tymon\JWTAuth\Facades\JWTAuth;
use Tymon\JWTAuth\Token;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Exceptions\JWTException;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Collection;

class JwtService
{
    /**
     * Generate JWT token for user
     */
    public function generateToken(User $user): string
    {
        try {
            $token = JWTAuth::fromUser($user);

            Log::info('JWT token generated successfully', [
                'user_id' => $user->_id,
                'token_preview' => substr($token, 0, 20) . '...'
            ]);

            return $token;
        } catch (\Exception $e) {
            Log::error('Failed to generate JWT token', [
                'user_id' => $user->_id,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }

    /**
     * Validate JWT token and return payload
     */
    public function validateToken(string $token): ?array
    {
        try {
            $payload = JWTAuth::setToken($token)->getPayload();

            if ($payload && !$this->isTokenExpired($payload)) {
                return $payload->toArray();
            }

            return null;
        } catch (\Exception $e) {
            Log::warning('JWT token validation failed', [
                'token_preview' => substr($token, 0, 20) . '...',
                'error' => $e->getMessage()
            ]);
            return null;
        }
    }

    /**
     * Refresh JWT token
     */
    public function refreshToken(): string
    {
        try {
            $newToken = JWTAuth::refresh();

            Log::info('JWT token refreshed successfully');

            return $newToken;
        } catch (\Exception $e) {
            Log::error('Failed to refresh JWT token', [
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }

    /**
     * Invalidate JWT token
     */
    public function invalidateToken(?string $token = null): bool
    {
        try {
            if ($token) {
                JWTAuth::setToken($token)->invalidate();
            } else {
                JWTAuth::invalidate();
            }

            Log::info('JWT token invalidated successfully');

            return true;
        } catch (\Exception $e) {
            Log::error('Failed to invalidate JWT token', [
                'error' => $e->getMessage()
            ]);
            return false;
        }
    }

    /**
     * Get authenticated user from token
     */
    public function getAuthenticatedUser(?string $token = null): ?User
    {
        try {
            if ($token) {
                return JWTAuth::setToken($token)->authenticate();
            }

            return JWTAuth::parseToken()->authenticate();
        } catch (\Exception $e) {
            Log::warning('Failed to get authenticated user from JWT token', [
                'error' => $e->getMessage()
            ]);
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    public function isTokenExpired(array $payload): bool
    {
        return isset($payload['exp']) && $payload['exp'] < time();
    }

    /**
     * Get token expiration time
     */
    public function getTokenExpirationTime(): int
    {
        return time() + (config('jwt.ttl') * 60);
    }

    /**
     * Get token TTL in minutes
     */
    public function getTokenTtl(): int
    {
        return config('jwt.ttl', 60);
    }

    /**
     * Create token response data
     */
    public function createTokenResponse(string $token): array
    {
        return [
            'token' => $token,
            'token_type' => 'bearer',
            'expires_in' => $this->getTokenTtl() * 60,
            'expires_at' => date('c', $this->getTokenExpirationTime())
        ];
    }

    /**
     * Extract token from request
     */
    public function extractTokenFromRequest(\Illuminate\Http\Request $request): ?string
    {
        // Check Authorization header
        $header = $request->header('Authorization');
        if ($header && preg_match('/Bearer\s+(.*)$/i', $header, $matches)) {
            return $matches[1];
        }

        // Check token parameter
        return $request->input('token');
    }

    /**
     * Verify token signature
     */
    public function verifyTokenSignature(string $token): bool
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
    public function getTokenClaims(?string $token = null): ?array
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
    public function isTokenBlacklisted(string $token): bool
    {
        try {
            // This would check against a blacklist if implemented
            return false; // Placeholder - implement blacklist checking if needed
        } catch (\Exception $e) {
            return true; // Consider blacklisted if error occurs
        }
    }
}
