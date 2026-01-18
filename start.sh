#!/bin/bash

# Ensure we're in the script's directory
cd "$(dirname "$0")"

echo "🚀 Starting Personal App..."

# Function to check if backends are ready
check_backends() {
    local backends=(
        "backend-api:http://localhost:8081/actuator/health"
        "backend-api-second:http://localhost:8010/"
    )

    for backend in "${backends[@]}"; do
        IFS=':' read -r name url <<< "$backend"
        local max_attempts=30
        local attempt=1

        echo "⏳ Waiting for $name to be ready..."

        while [ $attempt -le $max_attempts ]; do
            if curl -s --max-time 5 "$url" > /dev/null 2>&1; then
                echo "✅ $name is ready!"
                break
            fi

            echo "Attempt $attempt/$max_attempts: $name not ready yet..."
            sleep 10
            ((attempt++))
        done

        if [ $attempt -gt $max_attempts ]; then
            echo "❌ $name failed to start within expected time"
            return 1
        fi
    done

    echo "🎉 All backends are ready!"
    return 0
}

# Start backend-api services
echo "🏗️  Starting backend-api services..."
cd docker/backend-api
docker-compose up -d

# Start backend-api-second services
echo "🏗️  Starting backend-api-second services..."
cd ../backend-api-second/develop
docker-compose up -d

# Go back to root
cd ../..

# Wait for backends to be ready
if check_backends; then
    echo "🎉 Backends are fully started!"
    echo "🌐 Starting frontend..."

    cd frontend-client
    npm run dev
else
    echo "❌ Failed to start backend properly"
    exit 1
fi
