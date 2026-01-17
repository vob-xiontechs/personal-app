<?php

namespace App\Services\Auth\Jwt;

use Illuminate\Support\Facades\Facade;

/**
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
class JwtServiceFacade extends Facade
{
    /**
     * Get the registered name of the component.
     */
    protected static function getFacadeAccessor(): string
    {
        return JwtService::class;
    }
}
