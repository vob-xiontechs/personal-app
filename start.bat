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
) else if "%ENVIRONMENT%"=="staging" (
    set "BACKEND_PORT=8082"
    set "FRONTEND_PORT=3012"
    set "DOCKER_DIR=docker\staging"
) else (
    set "BACKEND_PORT=8081"
    set "FRONTEND_PORT=3011"
    set "DOCKER_DIR=docker\develop"
)

REM Update frontend config
echo ENVIRONMENT=%ENVIRONMENT%> "%ENV_FILE%"
echo VITE_API_BASE_URL=http://localhost:%BACKEND_PORT%>> "%ENV_FILE%"

REM ===== BACKEND BUILD =====
echo Building backend...

REM Show timeout warning
echo [INFO] Build timeout: 5 minutes (press Ctrl+C to cancel if needed)
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

REM ===== DEPLOYMENT =====
echo Starting backend server...
start "Backend" cmd /c "cd %BACKEND_DIR% && ./mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom\" -q"
timeout /t 3 >nul

REM ===== HEALTH CHECK =====
echo Waiting for backend...
set "HEALTH_URL=http://localhost:%BACKEND_PORT%/actuator/health"
set /a "attempt=0"
set /a "max_attempts=20"

:health_check
set /a "attempt+=1"
if %attempt% gtr %max_attempts% (
    echo Backend startup failed
    exit /b 1
)

curl -s --max-time 3 "%HEALTH_URL%" >nul 2>&1
if %errorlevel% equ 0 (
    echo Backend ready after %attempt% attempts
    goto :start_frontend
)

echo Health check %attempt%/%max_attempts%...
timeout /t 3 >nul
goto :health_check

:start_frontend
echo Starting frontend...
start "Backend-Health" "%HEALTH_URL%"
cd "%FRONTEND_DIR%"
start "Frontend" cmd /c "npm run dev"
timeout /t 3 >nul
start "App" "%FRONTEND_URL%"

echo.
echo ============================================
echo SUCCESS: %APP_NAME% started successfully!
echo ============================================
echo Backend: %HEALTH_URL%
echo Frontend: %FRONTEND_URL%
echo.
