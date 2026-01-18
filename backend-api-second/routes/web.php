<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\WelcomeController;

Route::get('/', [WelcomeController::class, 'index']);

Route::get('/docs', [WelcomeController::class, 'docs'])->name('docs');

Route::get('/api-docs.yaml', function () {
    $yamlContent = file_get_contents(storage_path('api-docs/api-docs-develop.yaml'));
    return response($yamlContent)->header('Content-Type', 'application/x-yaml');
});
