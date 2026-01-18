<?php

namespace App\Services\Auth;

use App\Models\User;
use App\Repositories\User\UserRepositoryInterface;
use App\Services\Auth\Jwt\JwtFacade;
use App\Utils\IdGeneratorUtil;
use App\Utils\JwtUtils;
use App\Constants\Auth\Messages;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;
use Illuminate\Support\Facades\Log;
use Tymon\JWTAuth\Facades\JWTAuth;

class AuthService
{
    public function __construct(
        private UserRepositoryInterface $userRepository
    ) {}

    /**
     * Register a new user
     */
    public function register(array $data): array
    {
        try {
            // Check if user already exists
            if ($this->userRepository->findByEmail($data['email'])) {
                throw ValidationException::withMessages([
                    'email' => [Messages::EMAIL_ALREADY_EXISTS]
                ]);
            }

            // Generate unique user_id using utility
            $userId = IdGeneratorUtil::generateUserId();

            // Create user
            $user = $this->userRepository->create([
                'user_id' => $userId,
                'name' => $data['name'],
                'email' => $data['email'],
                'password' => Hash::make($data['password']),
            ]);

            Log::info('User registered successfully', ['user_id' => $user->_id, 'email' => $user->email]);

            return [
                'user' => $user
            ];

        } catch (\Exception $e) {
            Log::error('User registration failed', [
                'email' => $data['email'] ?? null,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }

    /**
     * Authenticate user and return token
     */
    public function login(array $credentials): array
    {
        try {
            // Find user by email
            $user = $this->userRepository->findByEmail($credentials['email']);

            // Verify user exists and password is correct
            if (!$user || !Hash::check($credentials['password'], $user->password)) {
                throw ValidationException::withMessages([
                    'email' => [Messages::INVALID_LOGIN_CREDENTIALS]
                ]);
            }

            // Generate JWT token
            $token = JwtFacade::generateToken($user);

            Log::info('User logged in successfully', ['user_id' => $user->_id, 'email' => $user->email]);

            return [
                'user' => $user,
                'token' => $token,
                'token_type' => 'bearer',
                'expires_in' => config('jwt.ttl') * 60
            ];

        } catch (ValidationException $e) {
            throw $e;
        } catch (\Exception $e) {
            Log::error('User login failed', [
                'email' => $credentials['email'] ?? null,
                'error' => $e->getMessage()
            ]);
            throw ValidationException::withMessages([
                'email' => ['Login failed. Please try again.']
            ]);
        }
    }

    /**
     * Logout user by invalidating token
     */
    public function logout(): bool
    {
        try {
            JwtFacade::invalidateToken();
            Log::info('User logged out successfully');
            return true;
        } catch (\Exception $e) {
            Log::error('User logout failed', ['error' => $e->getMessage()]);
            return false;
        }
    }

    /**
     * Refresh JWT token
     */
    public function refresh(): array
    {
        try {
            $newToken = JwtFacade::refreshToken();

            Log::info('Token refreshed successfully');

            return [
                'token' => $newToken,
                'token_type' => 'bearer',
                'expires_in' => config('jwt.ttl') * 60
            ];

        } catch (\Exception $e) {
            Log::error('Token refresh failed', ['error' => $e->getMessage()]);
            throw ValidationException::withMessages([
                'token' => ['Token refresh failed.']
            ]);
        }
    }
}
