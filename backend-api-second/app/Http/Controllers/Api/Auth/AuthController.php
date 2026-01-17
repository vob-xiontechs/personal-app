<?php

namespace App\Http\Controllers\Api\Auth;

use App\Http\Controllers\Controller;
use App\Services\Auth\AuthFacade as AuthService;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Http\Resources\TblUserSdResource;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Symfony\Component\HttpFoundation\Response;

class AuthController extends Controller
{
    /**
     * Register a new user
     */
    public function register(RegisterRequest $request): JsonResponse
    {
        try {
            $result = AuthService::register($request->validated());

            Log::info('User registration API called successfully', [
                'email' => $request->email,
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => true,
                'message' => 'User registered successfully',
                'data' => [
                    'user' => new UserSdResource($result['user']),
                    'token' => $result['token'],
                    'token_type' => $result['token_type'],
                    'expires_in' => $result['expires_in']
                ]
            ], Response::HTTP_CREATED);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $e->errors()
            ], Response::HTTP_UNPROCESSABLE_ENTITY);

        } catch (\Exception $e) {
            Log::error('User registration API failed', [
                'email' => $request->email,
                'error' => $e->getMessage(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Registration failed. Please try again.'
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticate user and return JWT token
     */
    public function login(LoginRequest $request): JsonResponse
    {
        try {
            $result = AuthService::login($request->only(['email', 'password']));

            Log::info('User login API called successfully', [
                'email' => $request->email,
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Login successful',
                'data' => [
                    'user' => new UserSdResource($result['user']),
                    'token' => $result['token'],
                    'token_type' => $result['token_type'],
                    'expires_in' => $result['expires_in']
                ]
            ]);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid login credentials',
                'errors' => $e->errors()
            ], Response::HTTP_UNAUTHORIZED);

        } catch (\Exception $e) {
            Log::error('User login API failed', [
                'email' => $request->email,
                'error' => $e->getMessage(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Login failed. Please try again.'
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout user by invalidating JWT token
     */
    public function logout(Request $request): JsonResponse
    {
        try {
            $user = auth()->user();
            $logout = AuthService::logout();

            Log::info('User logout API called', [
                'user_id' => $user?->_id,
                'email' => $user?->email,
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Logout successful'
            ]);

        } catch (\Exception $e) {
            Log::error('User logout API failed', [
                'user_id' => auth()->id(),
                'error' => $e->getMessage(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Logout failed'
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Refresh JWT token
     */
    public function refresh(Request $request): JsonResponse
    {
        try {
            $result = AuthService::refresh();

            Log::info('Token refresh API called successfully', [
                'user_id' => auth()->id(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Token refreshed successfully',
                'data' => [
                    'token' => $result['token'],
                    'token_type' => $result['token_type'],
                    'expires_in' => $result['expires_in']
                ]
            ]);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Token refresh failed',
                'errors' => $e->errors()
            ], Response::HTTP_UNAUTHORIZED);

        } catch (\Exception $e) {
            Log::error('Token refresh API failed', [
                'user_id' => auth()->id(),
                'error' => $e->getMessage(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Token refresh failed'
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get authenticated user profile
     */
    public function profile(Request $request): JsonResponse
    {
        try {
            $user = AuthService::getProfile();

            return response()->json([
                'success' => true,
                'message' => 'User profile retrieved successfully',
                'data' => new TblUserSdResource($user)
            ]);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Unable to retrieve user profile',
                'errors' => $e->errors()
            ], Response::HTTP_UNAUTHORIZED);

        } catch (\Exception $e) {
            Log::error('Get profile API failed', [
                'user_id' => auth()->id(),
                'error' => $e->getMessage(),
                'ip' => $request->ip()
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Unable to retrieve user profile'
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}
