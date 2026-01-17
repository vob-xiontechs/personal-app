<?php

require_once __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';

$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

try {
    // Test basic connection by getting database info
    $db = DB::connection('mongodb');
    $database = $db->getMongoDB();

    // Simple test - just get database name
    $dbName = $database->getDatabaseName();
    echo "✅ Laravel MongoDB connection successful!\n";
    echo "Connected to database: " . $dbName . "\n";

    // Try a simple collection operation
    $collection = $database->selectCollection('test_collection');
    $collection->insertOne(['test' => 'data', 'timestamp' => new MongoDB\BSON\UTCDateTime()]);
    echo "✅ Successfully inserted test document!\n";

    // Count documents
    $count = $collection->countDocuments();
    echo "Collection has " . $count . " documents\n";

} catch (Exception $e) {
    echo "❌ Laravel MongoDB connection error: " . $e->getMessage() . "\n";
}
