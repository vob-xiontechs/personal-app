<?php

try {
    $manager = new MongoDB\Driver\Manager('mongodb://admin:mypassword123@mongodb:27017/backend_db_second');
    $command = new MongoDB\Driver\Command(['ping' => 1]);
    $cursor = $manager->executeCommand('admin', $command);
    $response = $cursor->toArray()[0];

    if ($response->ok == 1) {
        echo "✅ MongoDB connection successful!\n";
        echo "Response: " . json_encode($response) . "\n";
    } else {
        echo "❌ MongoDB ping failed\n";
    }
} catch (Exception $e) {
    echo "❌ MongoDB connection error: " . $e->getMessage() . "\n";
}
