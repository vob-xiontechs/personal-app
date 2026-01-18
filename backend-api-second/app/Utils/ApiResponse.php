<?php

namespace App\Utils;

use Illuminate\Http\JsonResponse;
use Symfony\Component\HttpFoundation\Response;

/**
 * API Response Helper
 */
class ApiResponse
{
    /**
     * Success response
     */
    public static function success(string $message, $data = null, int $statusCode = Response::HTTP_OK): JsonResponse
    {
        $response = [
            'success' => true,
            'message' => $message,
        ];

        if ($data !== null) {
            $response['data'] = $data;
        }

        return response()->json($response, $statusCode);
    }

    /**
     * Error response
     */
    public static function error(string $message, array $errors = [], int $statusCode = Response::HTTP_BAD_REQUEST): JsonResponse
    {
        $response = [
            'success' => false,
            'message' => $message,
        ];

        if (!empty($errors)) {
            $response['errors'] = $errors;
        }

        return response()->json($response, $statusCode);
    }

    /**
     * Validation error response
     */
    public static function validationError(string $message, array $errors): JsonResponse
    {
        return self::error($message, $errors, Response::HTTP_UNPROCESSABLE_ENTITY);
    }

    /**
     * Unauthorized response
     */
    public static function unauthorized(string $message = 'Unauthorized'): JsonResponse
    {
        return self::error($message, [], Response::HTTP_UNAUTHORIZED);
    }

    /**
     * Forbidden response
     */
    public static function forbidden(string $message = 'Forbidden'): JsonResponse
    {
        return self::error($message, [], Response::HTTP_FORBIDDEN);
    }

    /**
     * Not found response
     */
    public static function notFound(string $message = 'Not found'): JsonResponse
    {
        return self::error($message, [], Response::HTTP_NOT_FOUND);
    }

    /**
     * Internal server error response
     */
    public static function internalServerError(string $message = 'Internal server error'): JsonResponse
    {
        return self::error($message, [], Response::HTTP_INTERNAL_SERVER_ERROR);
    }
}
