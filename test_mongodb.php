<?php

// Test 1: Try without authentication first
echo "=== Test 1: Connection without authentication ===\n";
try {
    $manager = new MongoDB\Driver\Manager('mongodb://172.18.0.2:27017');
    $command = new MongoDB\Driver\Command(['ping' => 1]);
    $cursor = $manager->executeCommand('admin', $command);
    $response = $cursor->toArray()[0];

    if ($response->ok == 1) {
        echo "✅ MongoDB connection without auth successful!\n";
        echo "MongoDB is running without authentication.\n\n";
    }
} catch (Exception $e) {
    echo "❌ No-auth connection failed: " . $e->getMessage() . "\n\n";
}

// Test 2: Try with authentication
echo "=== Test 2: Connection with authentication ===\n";
try {
    $manager = new MongoDB\Driver\Manager('mongodb://root:root@172.18.0.2:27017/backend_db_second?authSource=admin');
    $command = new MongoDB\Driver\Command(['ping' => 1]);
    $cursor = $manager->executeCommand('admin', $command);
    $response = $cursor->toArray()[0];

    if ($response->ok == 1) {
        echo "✅ MongoDB connection with authentication successful!\n";

        // Try to list databases
        $command2 = new MongoDB\Driver\Command(['listDatabases' => 1]);
        $cursor2 = $manager->executeCommand('admin', $command2);
        $databases = $cursor2->toArray()[0];
        echo "Available databases: " . implode(', ', array_column($databases->databases, 'name')) . "\n";
    }
} catch (Exception $e) {
    echo "❌ Auth connection failed: " . $e->getMessage() . "\n";
}
