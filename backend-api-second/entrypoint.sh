#!/bin/bash

# Install dependencies if autoload.php doesn't exist
if [ ! -f "vendor/autoload.php" ]; then
    echo "Installing Composer dependencies..."
    composer install --no-dev --optimize-autoloader
fi

# Generate application key if not set
if [ -z "$APP_KEY" ] || [ "$APP_KEY" = "base64:" ]; then
    echo "Generating application key..."
    php artisan key:generate --no-interaction
fi

# Start Laravel development server
echo "Starting Laravel development server..."
php artisan serve --host=0.0.0.0 --port=8010
