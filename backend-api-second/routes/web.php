<?php

use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return 'Hello World';
});

Route::get('/docs', function () {
    return view('swagger-ui');
});

Route::get('/api-docs.yaml', function () {
    $yamlContent = file_get_contents(storage_path('api-docs/api-docs-develop.yaml'));
    return response($yamlContent)->header('Content-Type', 'application/x-yaml');
});
