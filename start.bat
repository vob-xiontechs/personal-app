@echo off
echo 🚀 Starting Personal App...

REM Read environment from .env
for /f "tokens=1,2 delims==" %%a in (frontend-client\.env) do set %%a=%%b

echo Environment: %ENVIRONMENT%

REM Set environment-specific variables
if "%ENVIRONMENT%"=="develop" (
    set DOCKER_DIR=docker\develop
    set BACKEND_PORT=8081
    set FRONTEND_PORT=3011
    set VITE_API_BASE_URL=http://localhost:8081
) else (
    if "%ENVIRONMENT%"=="staging" (
        set DOCKER_DIR=docker\staging
        set BACKEND_PORT=8082
        set FRONTEND_PORT=3011
        set VITE_API_BASE_URL=http://localhost:8082
    ) else (
        echo Invalid ENVIRONMENT in .env. Use 'develop' or 'staging'.
        exit /b 1
    )
)

REM Update .env with correct API URL
echo ENVIRONMENT=%ENVIRONMENT%> frontend-client\.env
echo VITE_API_BASE_URL=%VITE_API_BASE_URL%>> frontend-client\.env

REM Start backend services
echo 🏗️  Starting backend services...
cd %DOCKER_DIR%
docker-compose up --build -d

REM Go back to root directory
cd ..\..

REM Function to check if backend is ready
set url=http://localhost:%BACKEND_PORT%/actuator/health
set max_attempts=30
set attempt=1

echo ⏳ Waiting for backend to be ready...

:check_loop
if %attempt% gtr %max_attempts% goto :backend_failed

REM Check if backend health endpoint is accessible
curl -s --max-time 5 "%url%" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Backend is ready!
    goto :start_frontend
)

echo Attempt %attempt%/%max_attempts%: Backend not ready yet...
timeout /t 10 /nobreak >nul
set /a attempt+=1
goto :check_loop

:backend_failed
echo ❌ Backend failed to start within expected time
exit /b 1

:start_frontend
echo 🎉 Backend is fully started!
echo 🌐 Starting frontend...

REM Open backend in browser
start http://localhost:%BACKEND_PORT%/actuator/health

REM Start frontend
cd frontend-client
call npm run dev

REM Wait a moment for frontend to start, then open in browser
timeout /t 5 /nobreak >nul
start http://localhost:%FRONTEND_PORT%
