<?php

require_once __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';

$app->make(\Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use Illuminate\Support\Facades\DB;

try {
    echo "Testing User model with MongoDB...\n";

    // Test creating a user directly with the model
    $user = new \App\Models\User();
    $user->name = 'Test User';
    $user->email = 'test@example.com';
    $user->password = bcrypt('password123');
    $user->save();

    echo "✅ User created successfully with ID: " . $user->_id . "\n";

    // Test finding the user
    $foundUser = \App\Models\User::where('email', 'test@example.com')->first();
    if ($foundUser) {
        echo "✅ User found successfully: " . $foundUser->name . " (" . $foundUser->email . ")\n";
    } else {
        echo "❌ User not found\n";
    }

    // Test authentication methods
    echo "✅ Auth identifier: " . $foundUser->getAuthIdentifier() . "\n";
    echo "✅ Auth password exists: " . (!empty($foundUser->getAuthPassword()) ? 'yes' : 'no') . "\n";

    echo "\n🎉 User model with MongoDB working correctly!\n";

} catch (Exception $e) {
    echo "❌ User model error: " . $e->getMessage() . "\n";
    echo "File: " . $e->getFile() . ":" . $e->getLine() . "\n";
}
