<?php

namespace App\Http\Controllers\Api\Auth;

use App\Http\Controllers\Controller;
use App\Services\Auth\AuthFacade as AuthService;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Http\Resources\UserResource;
use App\Constants\Auth\Messages;
use App\Utils\ApiResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

/**
 * @OA\Tag(
 *     name="Authentication",
 *     description="API Endpoints for User Authentication"
 * )
 */
class AuthController extends Controller
{
    /**
     * Register a new user
     *
     * @OA\Post(
     *     path="/api/auth/register",
     *     summary="Register a new user",
     *     tags={"Authentication"},
     *     @OA\RequestBody(
     *         required=true,
     *         @OA\JsonContent(
     *             required={"name","email","password"},
     *             @OA\Property(property="name", type="string", example="John Doe"),
     *             @OA\Property(property="email", type="string", format="email", example="john@example.com"),
     *             @OA\Property(property="password", type="string", minLength=8, example="password123")
     *         )
     *     ),
     *     @OA\Response(
     *         response=201,
     *         description="User registered successfully",
     *         @OA\JsonContent(
     *             @OA\Property(property="success", type="boolean", example=true),
     *             @OA\Property(property="message", type="string", example="User registered successfully"),
     *             @OA\Property(property="data", ref="#/components/schemas/User")
     *         )
     *     ),
     *     @OA\Response(
     *         response=422,
     *         description="Validation failed",
     *         @OA\JsonContent(
     *             @OA\Property(property="success", type="boolean", example=false),
     *             @OA\Property(property="message", type="string", example="Validation failed"),
     *             @OA\Property(property="errors", type="object")
     *         )
     *     ),
     *     @OA\Response(
     *         response=500,
     *         description="Registration failed",
     *         @OA\JsonContent(
     *             @OA\Property(property="success", type="boolean", example=false),
     *             @OA\Property(property="message", type="string", example="Registration failed. Please try again.")
     *         )
     *     )
     * )
     */
    public function register(RegisterRequest $request): JsonResponse
    {
        try {
            $result = AuthService::register($request->validated());

            return ApiResponse::success(Messages::USER_REGISTERED_SUCCESSFULLY, new UserResource($result['user']), Response::HTTP_CREATED);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return ApiResponse::validationError(Messages::VALIDATION_FAILED, $e->errors());

        } catch (\Exception $e) {
            return ApiResponse::internalServerError(Messages::REGISTRATION_FAILED);
        }
    }

    /**
     * Authenticate user and return JWT token
     */
    public function login(LoginRequest $request): JsonResponse
    {
        try {
            $result = AuthService::login($request->only(['email', 'password']));

            return response()->json([
                'success' => true,
                'message' => Messages::LOGIN_SUCCESSFUL,
                'token' => $result['token']
            ]);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return ApiResponse::unauthorized(Messages::INVALID_LOGIN_CREDENTIALS);

        } catch (\Exception $e) {
            return ApiResponse::internalServerError(Messages::LOGIN_FAILED);
        }
    }

    /**
     * Logout user by invalidating JWT token
     */
    public function logout(Request $request): JsonResponse
    {
        try {
            AuthService::logout();

            return ApiResponse::success(Messages::LOGOUT_SUCCESSFUL);

        } catch (\Exception $e) {
            return ApiResponse::internalServerError(Messages::LOGOUT_FAILED);
        }
    }

    /**
     * Refresh JWT token
     */
    public function refresh(Request $request): JsonResponse
    {
        try {
            $result = AuthService::refresh();

            return ApiResponse::success(Messages::TOKEN_REFRESHED_SUCCESSFULLY, [
                'token' => $result['token'],
                'token_type' => $result['token_type'],
                'expires_in' => $result['expires_in']
            ]);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return ApiResponse::unauthorized(Messages::TOKEN_REFRESH_FAILED);

        } catch (\Exception $e) {
            return ApiResponse::internalServerError(Messages::TOKEN_REFRESH_FAILED);
        }
    }
}
