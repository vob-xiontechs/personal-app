<?php

use App\Http\Controllers\Api\Auth\AuthController;
use App\Http\Controllers\Api\User\UserController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

// Public Authentication Routes (No Auth Required)
Route::prefix('auth')->group(function () {
    Route::post('register', [AuthController::class, 'register']);
    Route::post('login', [AuthController::class, 'login']);
});

// Protected Authentication Routes (Auth Required)
Route::middleware('jwt.auth')->prefix('auth')->group(function () {
    Route::post('logout', [AuthController::class, 'logout']);
    Route::post('refresh', [AuthController::class, 'refresh']);
});

// Protected User Routes (Auth Required)
Route::middleware('jwt.auth')->prefix('user')->group(function () {
    Route::get('profile', [UserController::class, 'profile']);
});

// Health Check Route
Route::get('health', function () {
    return response()->json([
        'status' => 'ok',
        'timestamp' => now()->toISOString(),
        'version' => '1.0.0'
    ]);
});

// Test Route
Route::get('test', function () {
    return response()->json([
        'message' => 'Test route works',
        'timestamp' => now()->toISOString()
    ]);
});

// Simple register test
Route::post('register-test', function (Request $request) {
    try {
        $data = $request->all();

        // Basic validation
        if (empty($data['name']) || empty($data['email']) || empty($data['password'])) {
            return response()->json([
                'error' => 'Missing required fields',
                'received' => $data
            ], 400);
        }

        // Create user directly
        $userRepository = app(\App\Repositories\User\UserRepositoryInterface::class);
        $user = $userRepository->create([
            'name' => $data['name'],
            'email' => $data['email'],
            'password' => bcrypt($data['password'])
        ]);

        return response()->json([
            'success' => true,
            'message' => 'User registered successfully',
            'user_id' => (string) $user->_id
        ]);
    } catch (\Exception $e) {
        return response()->json([
            'error' => 'Registration failed: ' . $e->getMessage()
        ], 500);
    }
});
