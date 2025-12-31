#!/bin/bash

echo "🚀 Starting Personal App..."

# Function to check if backend is ready
check_backend() {
    local url="http://localhost:8081/actuator/health"
    local max_attempts=30
    local attempt=1

    echo "⏳ Waiting for backend to be ready..."

    while [ $attempt -le $max_attempts ]; do
        if curl -s --max-time 5 "$url" > /dev/null 2>&1; then
            echo "✅ Backend is ready!"
            return 0
        fi

        echo "Attempt $attempt/$max_attempts: Backend not ready yet..."
        sleep 10
        ((attempt++))
    done

    echo "❌ Backend failed to start within expected time"
    return 1
}

# Start backend services
echo "🏗️  Starting backend services..."
cd docker/develop
docker-compose up -d

# Wait for backend to be ready
if check_backend; then
    echo "🎉 Backend is fully started!"
    echo "🌐 Starting frontend..."

    # Go back to root and start frontend
    cd ../..
    cd frontend-client
    npm run dev
else
    echo "❌ Failed to start backend properly"
    exit 1
fi
