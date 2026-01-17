<?php

namespace App\Services\Auth;

use App\Models\TblUserSd;
use App\Repositories\Auth\UserRepositoryInterface;
use Illuminate\Support\Facades\Hash;
use Tymon\JWTAuth\Facades\JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use Illuminate\Validation\ValidationException;
use Illuminate\Support\Facades\Log;

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
                    'email' => ['Email đã được sử dụng.']
                ]);
            }

            // Create user
            $user = $this->userRepository->create([
                'name' => $data['name'],
                'email' => $data['email'],
                'password' => Hash::make($data['password']),
            ]);

            // Generate JWT token
            $token = JWTAuth::fromUser($user);

            Log::info('User registered successfully', ['user_id' => $user->_id, 'email' => $user->email]);

            return [
                'user' => $user,
                'token' => $token,
                'token_type' => 'bearer',
                'expires_in' => config('jwt.ttl') * 60
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
            if (!$token = JWTAuth::attempt($credentials)) {
                throw ValidationException::withMessages([
                    'email' => ['Email hoặc mật khẩu không đúng.']
                ]);
            }

            $user = JWTAuth::user();

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
                'email' => ['Đăng nhập thất bại. Vui lòng thử lại.']
            ]);
        }
    }

    /**
     * Logout user by invalidating token
     */
    public function logout(): bool
    {
        try {
            JWTAuth::invalidate(JWTAuth::getToken());
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
            $newToken = JWTAuth::refresh();
            $user = JWTAuth::user();

            Log::info('Token refreshed successfully', ['user_id' => $user->_id]);

            return [
                'token' => $newToken,
                'token_type' => 'bearer',
                'expires_in' => config('jwt.ttl') * 60
            ];

        } catch (\Exception $e) {
            Log::error('Token refresh failed', ['error' => $e->getMessage()]);
            throw ValidationException::withMessages([
                'token' => ['Token refresh thất bại.']
            ]);
        }
    }

    /**
     * Get authenticated user profile
     */
    public function getProfile(): TblUserSd
    {
        try {
            return JWTAuth::user();
        } catch (\Exception $e) {
            Log::error('Get profile failed', ['error' => $e->getMessage()]);
            throw ValidationException::withMessages([
                'user' => ['Không thể lấy thông tin người dùng.']
            ]);
        }
    }
}
