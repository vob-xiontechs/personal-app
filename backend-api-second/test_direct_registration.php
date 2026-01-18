<?php

require_once __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';

$app->make(\Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use App\Services\Auth\AuthService;
use Illuminate\Support\Facades\Log;

try {
    echo "Testing user registration with AuthService...\n";

    // Create AuthService instance (it will resolve dependencies automatically)
    $authService = app(AuthService::class);

    // Test user registration through AuthService with unique email
    $uniqueId = time() . rand(100, 999);
    $userData = [
        'name' => 'Direct Test User',
        'email' => 'direct-test-' . $uniqueId . '@example.com',
        'password' => 'password123',
    ];

    $result = $authService->register($userData);
    $user = $result['user'];

    echo "✅ User created successfully!\n";
    echo "- MongoDB _id: " . ($user->_id ?? 'N/A') . "\n";
    echo "- Custom user_id: " . ($user->user_id ?? 'N/A') . "\n";
    echo "- Name: " . ($user->name ?? 'N/A') . "\n";
    echo "- Email: " . ($user->email ?? 'N/A') . "\n";

    // Test finding the user
    $userRepository = app(\App\Repositories\Auth\UserRepositoryInterface::class);
    $foundUser = $userRepository->findByEmail($userData['email']);

    if ($foundUser) {
        echo "\n✅ User found successfully!\n";
        echo "- MongoDB _id: " . ($foundUser->_id ?? 'N/A') . "\n";
        echo "- Custom user_id: " . ($foundUser->user_id ?? 'N/A') . "\n";
        echo "- Name: " . ($foundUser->name ?? 'N/A') . "\n";
        echo "- Email: " . ($foundUser->email ?? 'N/A') . "\n";
    } else {
        echo "\n❌ User not found!\n";
    }

    // Test login functionality
    echo "\n🔐 Testing login functionality...\n";

    $loginData = [
        'email' => $userData['email'],
        'password' => 'password123', // Same password used for registration
    ];

    // Debug: Check if user exists and password is correct
    echo "🔍 Debugging login process...\n";

    // Check if user exists
    $userCheck = $userRepository->findByEmail($loginData['email']);
    if ($userCheck) {
        echo "✅ User found by email: " . $userCheck->email . "\n";
        echo "   - Stored password hash: " . substr($userCheck->password, 0, 20) . "...\n";

        // Test password verification directly
        $passwordCheck = password_verify($loginData['password'], $userCheck->password);
        echo "   - Password verification: " . ($passwordCheck ? 'SUCCESS' : 'FAILED') . "\n";

        // Test Laravel Hash check
        $laravelHashCheck = \Illuminate\Support\Facades\Hash::check($loginData['password'], $userCheck->password);
        echo "   - Laravel Hash check: " . ($laravelHashCheck ? 'SUCCESS' : 'FAILED') . "\n";
    } else {
        echo "❌ User NOT found by email: " . $loginData['email'] . "\n";
    }



    try {
        $loginResult = $authService->login($loginData);

        if (isset($loginResult['token'])) {
            echo "✅ Login successful!\n";
            echo "- Token: " . substr($loginResult['token'], 0, 50) . "...\n";
            echo "- Token Type: " . ($loginResult['token_type'] ?? 'N/A') . "\n";
            echo "- Expires In: " . ($loginResult['expires_in'] ?? 'N/A') . " seconds\n";

            // Test JWT token authentication
            echo "\n🔐 Testing JWT token authentication...\n";

            // First, decode the token to see what it contains
            try {
                $jwtAuth = app('tymon.jwt.auth');
                $payload = $jwtAuth->setToken($loginResult['token'])->getPayload();

                echo "📝 JWT Token Payload:\n";
                echo "- Subject (sub): " . ($payload->get('sub') ?? 'N/A') . "\n";
                echo "- Issuer (iss): " . ($payload->get('iss') ?? 'N/A') . "\n";
                echo "- Expires (exp): " . ($payload->get('exp') ?? 'N/A') . "\n";

                // Now try to authenticate
                $user = $jwtAuth->authenticate();

                if ($user) {
                    echo "✅ JWT authentication successful!\n";
                    echo "- Authenticated user: " . $user->email . "\n";
                    echo "- User ID: " . ($user->user_id ?? 'N/A') . "\n";
                    echo "- MongoDB ID: " . ($user->_id ?? 'N/A') . "\n";
                } else {
                    echo "❌ JWT authentication failed - no user returned\n";

                    // Try to find user manually with the subject
                    $subject = $payload->get('sub');
                    echo "- Attempting manual lookup with subject: {$subject}\n";

                    $manualUser = app(\App\Repositories\Auth\UserRepositoryInterface::class)->findById($subject);
                    if ($manualUser) {
                        echo "- ✅ Manual lookup successful: " . $manualUser->email . "\n";
                    } else {
                        echo "- ❌ Manual lookup failed\n";
                    }
                }

                // Test getProfile method directly
                echo "\n👤 Testing getProfile method...\n";
                try {
                    // Set the token in JWTAuth context
                    JWTAuth::setToken($loginResult['token']);

                    $profileUser = $authService->getProfile();
                    if ($profileUser) {
                        echo "✅ getProfile successful!\n";
                        echo "- Profile user: " . $profileUser->email . "\n";
                        echo "- Profile user_id: " . ($profileUser->user_id ?? 'N/A') . "\n";
                        echo "- Profile MongoDB ID: " . ($profileUser->_id ?? 'N/A') . "\n";
                    } else {
                        echo "❌ getProfile failed - no user returned\n";
                    }
                } catch (\Exception $e) {
                    echo "❌ getProfile error: " . $e->getMessage() . "\n";
                }
            } catch (\Exception $e) {
                echo "❌ JWT authentication error: " . $e->getMessage() . "\n";
                echo "   File: " . $e->getFile() . ":" . $e->getLine() . "\n";
            }
        } else {
            echo "❌ Login failed - no token returned\n";
        }
    } catch (\Exception $e) {
        echo "❌ Login failed: " . $e->getMessage() . "\n";
    }

    echo "\n🎉 Direct MongoDB operations working correctly!\n";

} catch (\Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
    echo "File: " . $e->getFile() . ":" . $e->getLine() . "\n";
}
