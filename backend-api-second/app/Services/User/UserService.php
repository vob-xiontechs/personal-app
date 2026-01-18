<?php

namespace App\Services\User;

use App\Models\User;
use App\Repositories\User\UserRepositoryInterface;
use App\Utils\JwtUtils;
use Illuminate\Validation\ValidationException;
use Illuminate\Support\Facades\Log;

class UserService
{
    public function __construct(
        private UserRepositoryInterface $userRepository
    ) {}

    /**
     * Get authenticated user profile
     */
    public function getProfile(): User
    {
        try {
            // Get the JWT auth instance (middleware has already set the token)
            $jwtAuth = app('tymon.jwt.auth');

            // Get the token string and use JwtUtils to process it
            $token = $jwtAuth->getToken();
            $payload = JwtUtils::decodePayload($token);

            if (!$payload) {
                throw ValidationException::withMessages([
                    'user' => ['Invalid token: unable to decode payload.']
                ]);
            }

            // Extract user_id from JWT payload using JwtUtils
            $userId = JwtUtils::getSubject($payload);

            if (!$userId) {
                throw ValidationException::withMessages([
                    'user' => ['Invalid token: missing user identifier.']
                ]);
            }

            // Validate token expiration using JwtUtils
            if (JwtUtils::isExpiringSoon($payload, 0)) {
                Log::warning('Token has expired or is expiring soon', [
                    'user_id' => $userId,
                    'remaining_time' => JwtUtils::getRemainingTime($payload)
                ]);
                throw ValidationException::withMessages([
                    'user' => ['Token has expired.']
                ]);
            }

            // Find user by user_id (prioritized lookup)
            $user = $this->userRepository->findById($userId);

            if (!$user) {
                throw ValidationException::withMessages([
                    'user' => ['User not found.']
                ]);
            }

            return $user;
        } catch (ValidationException $e) {
            throw $e;
        } catch (\Exception $e) {
            Log::error('Get profile failed', [
                'error' => $e->getMessage()
            ]);
            throw ValidationException::withMessages([
                'user' => ['Unable to retrieve user profile.']
            ]);
        }
    }
}
