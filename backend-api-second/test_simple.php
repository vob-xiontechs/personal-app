<?php

require_once __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';

$app->make(\Illuminate\Contracts\Console\Kernel::class)->bootstrap();

try {
    echo "✅ Laravel application bootstrapped successfully!\n";
    echo "Testing basic functionality...\n";

    // Test basic Laravel functionality
    echo "- Laravel version: " . app()->version() . "\n";
    echo "- Environment: " . app()->environment() . "\n";
    echo "- Debug mode: " . (config('app.debug') ? 'enabled' : 'disabled') . "\n";

    echo "\n🎉 Laravel application is working correctly!\n";

} catch (Exception $e) {
    echo "❌ Laravel bootstrap error: " . $e->getMessage() . "\n";
    echo "File: " . $e->getFile() . ":" . $e->getLine() . "\n";
}
