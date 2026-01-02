@echo off
setlocal enabledelayedexpansion

REM ============================================
REM Personal App - Optimized Startup Script
REM Sequential execution with smart fallbacks
REM ============================================

REM ===== CONFIG =====
set "APP_NAME=Personal App"
set "DEFAULT_ENV=develop"
set "ENV_FILE=frontend-client\.env"
set "BACKEND_DIR=backend-api"
set "FRONTEND_DIR=frontend-client"
set "HEALTH_URL=http://localhost:8081/actuator/health"
set "FRONTEND_URL=http://localhost:3011"

REM ===== INIT =====
echo Starting %APP_NAME%...

REM Read environment
for /f "tokens=1,2 delims==" %%a in (%ENV_FILE%) do (
    if "%%a"=="ENVIRONMENT" set "ENVIRONMENT=%%b"
)

REM Set defaults
if "%ENVIRONMENT%"=="" set "ENVIRONMENT=%DEFAULT_ENV%"

REM Configure environment
if "%ENVIRONMENT%"=="develop" (
    set "BACKEND_PORT=8081"
    set "FRONTEND_PORT=3011"
    set "DOCKER_DIR=docker\develop"
    set "ENV_DISPLAY=DEVELOP"
    set "ENV_COLOR=[92m"
) else if "%ENVIRONMENT%"=="staging" (
    set "BACKEND_PORT=8082"
    set "FRONTEND_PORT=3012"
    set "DOCKER_DIR=docker\staging"
    set "ENV_DISPLAY=STAGING"
    set "ENV_COLOR=[93m"
) else (
    set "BACKEND_PORT=8081"
    set "FRONTEND_PORT=3011"
    set "DOCKER_DIR=docker\develop"
    set "ENV_DISPLAY=UNKNOWN"
    set "ENV_COLOR=[91m"
)

REM Display current environment
echo.
echo ============================================
echo ENVIRONMENT: %ENV_DISPLAY%
echo Backend Port: %BACKEND_PORT%
echo Frontend Port: %FRONTEND_PORT%
echo ============================================
echo.

REM Update frontend config
echo ENVIRONMENT=%ENVIRONMENT%> "%ENV_FILE%"
echo VITE_API_BASE_URL=http://localhost:%BACKEND_PORT%>> "%ENV_FILE%"

REM ===== BACKEND BUILD =====
echo Building backend for %ENV_DISPLAY% environment...

REM Show timeout warning
echo [INFO] Build timeout: 5 minutes (press Ctrl+C to cancel if needed)
echo [INFO] Environment: %ENVIRONMENT% / Profile: %ENVIRONMENT%
cd "%BACKEND_DIR%"

REM Incremental build if JAR exists
if exist "target\*.jar" (
    echo Incremental build...
    call ./mvnw.cmd package -DskipTests -Dmaven.compiler.useIncrementalCompilation=true -q
) else (
    echo Clean build...
    call ./mvnw.cmd clean package -DskipTests -q
)

REM Verify build success
if errorlevel 1 (
    echo [ERROR] Backend build failed
    cd ..
    exit /b 1
)

REM Verify JAR exists
if not exist "target\*.jar" (
    echo [ERROR] Build completed but no JAR file found
    cd ..
    exit /b 1
)

echo [OK] Backend build completed successfully
cd ..

REM ===== DOCKER CLEANUP =====
echo Cleaning up existing Docker containers and images...
cd "%DOCKER_DIR%"

REM Stop and remove containers for this project only
echo Stopping project containers...
docker-compose down -v --remove-orphans 2>nul

REM Remove specific containers by name pattern
echo Removing project containers...
docker rm -f personal-app-mysql 2>nul
docker rm -f personal-app-backend-api 2>nul
docker rm -f personal-app-backend 2>nul

REM Remove specific images for this project
echo Removing old project images...
docker rmi personal-app-backend-api 2>nul
docker rmi personal-app-backend-api:latest 2>nul
docker rmi mysql:8.0 2>nul

REM Clean up dangling resources (optional, not destructive)
echo Cleaning up dangling resources...
docker system prune -f 2>nul

REM Build and start Docker containers
echo Building and starting Docker containers...
docker-compose up -d --build
if errorlevel 1 (
    echo [ERROR] Failed to start Docker containers
    cd ..
    exit /b 1
)
echo [OK] Docker containers started successfully

REM Show real-time container logs in a separate window
echo Starting real-time container log monitoring...
start "Docker Logs" cmd /c "docker-compose logs -f"
echo [OK] Docker cleanup completed

cd ..

REM ===== DEPLOYMENT =====
echo Starting backend server...
echo [DEBUG] Starting Spring Boot with profile: %ENVIRONMENT%
start "Backend" cmd /c "cd %BACKEND_DIR% && ./mvnw.cmd spring-boot:run -Dspring.profiles.active=%ENVIRONMENT% -Dspring-boot.run.jvmArguments=\"-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom\" -q"
echo [DEBUG] Backend start command issued, waiting 5 seconds...
timeout /t 5 >nul

REM ===== HEALTH CHECK =====
echo Waiting for backend...
set "HEALTH_URL=http://localhost:%BACKEND_PORT%/actuator/health"
set /a "attempt=0"
set /a "max_attempts=20"

:health_check_loop
set /a "attempt+=1"
if %attempt% gtr %max_attempts% (
    echo [ERROR] Backend startup failed after %max_attempts% attempts
    echo [ERROR] Check backend logs for details
    exit /b 1
)

echo Health check %attempt%/%max_attempts%...
curl -s --max-time 3 "%HEALTH_URL%" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Backend ready after %attempt% attempts
    goto start_frontend
)

timeout /t 3 >nul
goto health_check_loop

:start_frontend
echo Starting frontend...
start "Backend-Health" "%HEALTH_URL%"
cd "%FRONTEND_DIR%"

REM Check if frontend is already running on the expected port
netstat -ano | findstr ":%FRONTEND_PORT% " | findstr LISTENING >nul
if %errorlevel% equ 0 (
    echo Frontend is already running on port %FRONTEND_PORT%
) else (
    echo Starting frontend development server on port %FRONTEND_PORT%
    start "Frontend" cmd /c "npm run dev"
    timeout /t 5 >nul
)

REM Open the application in browser
start "App" "%FRONTEND_URL%"

echo.
echo ============================================
echo SUCCESS: %APP_NAME% started successfully!
echo ============================================
echo Backend: %HEALTH_URL%
echo Frontend: %FRONTEND_URL%
echo.
