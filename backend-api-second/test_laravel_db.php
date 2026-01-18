<?php

require_once __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';

$app->make(\Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use Illuminate\Support\Facades\DB;

try {
    // Test MongoDB connection via Laravel
    $connection = DB::connection('mongodb');
    $databases = $connection->getMongoClient()->listDatabases();

    echo "✅ Laravel MongoDB connection successful!\n";
    echo "Available databases:\n";

    foreach ($databases as $database) {
        echo "- " . $database->getName() . "\n";
    }

    // Test database operations
    echo "\nTesting database operations on 'backend_db_second':\n";

    // Get the database
    $db = $connection->getMongoClient()->selectDatabase('backend_db_second');
    echo "- Selected database: backend_db_second\n";

    // List collections
    $collections = $db->listCollections();
    echo "- Collections in backend_db_second:\n";
    foreach ($collections as $collection) {
        echo "  * " . $collection->getName() . "\n";
    }

    // Test inserting a document
    $collection = $db->selectCollection('test_connection');
    $result = $collection->insertOne([
        'message' => 'Laravel MongoDB connection test successful',
        'timestamp' => new \MongoDB\BSON\UTCDateTime(),
        'test_id' => uniqid()
    ]);

    echo "- Inserted test document with ID: " . $result->getInsertedId() . "\n";

    echo "\n🎉 All MongoDB operations completed successfully!\n";

} catch (Exception $e) {
    echo "❌ Laravel MongoDB connection error: " . $e->getMessage() . "\n";
    echo "File: " . $e->getFile() . ":" . $e->getLine() . "\n";
}
