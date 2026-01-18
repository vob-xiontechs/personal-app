<?php

namespace App\Http\Controllers\Api\User;

use App\Http\Controllers\Controller;
use App\Services\User\UserFacade as UserService;
use App\Http\Resources\UserResource;
use App\Constants\User\Messages;
use App\Utils\ApiResponse;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

/**
 * @OA\Tag(
 *     name="User",
 *     description="API Endpoints for User Management"
 * )
 */
class UserController extends Controller
{
    /**
     * Get authenticated user profile
     *
     * @OA\Get(
     *     path="/api/user/profile",
     *     summary="Get authenticated user profile",
     *     tags={"User"},
     *     security={{"bearerAuth":{}}},
     *     @OA\Response(
     *         response=200,
     *         description="User profile retrieved successfully",
     *         @OA\JsonContent(
     *             @OA\Property(property="success", type="boolean", example=true),
     *             @OA\Property(property="message", type="string", example="User profile retrieved successfully"),
     *             @OA\Property(property="data", ref="#/components/schemas/User")
     *         )
     *     ),
     *     @OA\Response(
     *         response=401,
     *         description="Unauthorized",
     *         @OA\JsonContent(
     *             @OA\Property(property="success", type="boolean", example=false),
     *             @OA\Property(property="message", type="string", example="Unable to retrieve user profile"),
     *             @OA\Property(property="errors", type="object")
     *         )
     *     ),
     *     @OA\Response(
     *         response=500,
     *         description="Internal server error",
     *         @OA\JsonContent(
     *             @OA\Property(property="success", type="boolean", example=false),
     *             @OA\Property(property="message", type="string", example="Unable to retrieve user profile")
     *         )
     *     )
     * )
     */
    public function profile(Request $request): JsonResponse
    {
        try {
            $user = UserService::getProfile();

            return ApiResponse::success(Messages::USER_PROFILE_RETRIEVED_SUCCESSFULLY, new UserResource($user));

        } catch (\Illuminate\Validation\ValidationException $e) {
            return ApiResponse::unauthorized(Messages::UNABLE_TO_RETRIEVE_USER_PROFILE);

        } catch (\Exception $e) {
            return ApiResponse::internalServerError(Messages::UNABLE_TO_RETRIEVE_USER_PROFILE);
        }
    }
}
