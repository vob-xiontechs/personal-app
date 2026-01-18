@echo off
setlocal enabledelayedexpansion

REM ============================================
REM Personal App - Update Script (No Cleanup)
REM Updates containers without deleting images/containers/volumes
REM ============================================

REM ===== CONFIG =====
set "APP_NAME=Personal App"
set "DEFAULT_ENV=develop"
set "ENV_FILE=frontend-client\.env"
set "BACKEND_DIR=backend-api"
set "FRONTEND_DIR=frontend-client"

REM ===== INIT =====
echo Starting %APP_NAME% update...

REM Read environment first
for /f "tokens=1,2 delims==" %%a in (%ENV_FILE%) do (
    if "%%a"=="ENVIRONMENT" set "ENVIRONMENT=%%b"
)

REM Set defaults
if "%ENVIRONMENT%"=="" set "ENVIRONMENT=%DEFAULT_ENV%"

REM Configure environment
if "%ENVIRONMENT%"=="develop" (
    set "BACKEND_PORT=8081"
    set "FRONTEND_PORT=3011"
    set "DB_PORT=3307"
    set "DOCKER_DIR=docker\backend-api\develop"
    set "ENV_DISPLAY=DEVELOP"
    set "ENV_COLOR=[92m"
) else if "%ENVIRONMENT%"=="staging" (
    set "BACKEND_PORT=8082"
    set "FRONTEND_PORT=3012"
    set "DB_PORT=3308"
    set "DOCKER_DIR=docker\backend-api\staging"
    set "ENV_DISPLAY=STAGING"
    set "ENV_COLOR=[93m"
) else (
    set "BACKEND_PORT=8081"
    set "FRONTEND_PORT=3011"
    set "DB_PORT=3307"
    set "DOCKER_DIR=docker\backend-api\develop"
    set "ENV_DISPLAY=UNKNOWN"
    set "ENV_COLOR=[91m"
)

REM Set URLs based on configured ports
set "HEALTH_URL=http://localhost:!BACKEND_PORT!/actuator/health"
set "FRONTEND_URL=http://localhost:!FRONTEND_PORT!"

REM ===== DATABASE BACKUP =====
echo Creating backup before update...
echo [DEBUG] About to cd to DOCKER_DIR: %DOCKER_DIR%
cd "%DOCKER_DIR%"
echo [DEBUG] After cd to DOCKER_DIR, current dir: %CD%

REM Always create backup before updates
echo [INFO] Creating backup before update operations...
docker-compose --profile backup up db-backup >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Database backup completed successfully before update
    REM Verify backup file was created
    for /f %%i in ('dir /b /o-d backup\backup_%ENVIRONMENT%_*.sql 2^>nul') do (
        echo [OK] Database backup file created: %%i
        goto :db_backup_done
    )
    echo [WARNING] Database backup command succeeded but no new file found
) else (
    echo [ERROR] Database backup failed before update
)

:db_backup_done
REM Backup environment files
echo [INFO] Creating environment files backup...
set "TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=%TIMESTAMP: =0%"

if not exist "backup\env" mkdir "backup\env"

REM Backup main frontend .env
if exist "..\..\..\frontend-client\.env" (
    copy "..\..\..\frontend-client\.env" "backup\env\.env_frontend_%ENVIRONMENT%_%TIMESTAMP%" >nul
    echo [OK] Frontend .env backed up
)

REM Backup backend .env if exists
if exist "..\..\..\backend-api\.env" (
    copy "..\..\..\backend-api\.env" "backup\env\.env_backend_%ENVIRONMENT%_%TIMESTAMP%" >nul
    echo [OK] Backend .env backed up
)

REM Backup second backend .env
if exist "..\..\..\backend-api-second\.env" (
    copy "..\..\..\backend-api-second\.env" "backup\env\.env_backend_second_%ENVIRONMENT%_%TIMESTAMP%" >nul
    echo [OK] Second backend .env backed up
)

echo [OK] Environment files backup completed
:backup_done

echo [DEBUG] About to cd back to project root
cd ..\..\..
echo [DEBUG] After cd back, current dir: %CD%

REM Display current environment
echo.
echo ============================================
echo ENVIRONMENT: %ENV_DISPLAY%
echo OPERATION: UPDATE (No Cleanup)
echo Database Port: %DB_PORT%
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
echo [DEBUG] Current directory before cd: %CD%
echo [DEBUG] Attempting to cd to: %BACKEND_DIR%
cd "%BACKEND_DIR%"
echo [DEBUG] Current directory after cd: %CD%

REM Incremental build if JAR exists
if exist "target\*.jar" (
    echo Incremental build...
    call mvnw.cmd package -DskipTests -Dmaven.compiler.useIncrementalCompilation=true -q
) else (
    echo Clean build...
    call mvnw.cmd clean package -DskipTests -q
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

REM ===== DOCKER UPDATE =====
echo Checking Docker containers status...
cd "%DOCKER_DIR%"

REM Check which containers are running
echo Checking main backend container...
docker ps --filter "name=backend-api-%ENVIRONMENT%" --filter "status=running" -q >nul 2>&1
if !errorlevel! equ 0 (
    echo [OK] Main backend container is running
) else (
    echo [INFO] Main backend container not found, will rebuild...
    set "REBUILD_MAIN=1"
)

REM Check database container
echo Checking database container...
docker ps --filter "name=mysql-%ENVIRONMENT%" --filter "status=running" -q >nul 2>&1
if !errorlevel! equ 0 (
    echo [OK] Database container is running
) else (
    echo [INFO] Database container not found, will rebuild...
    set "REBUILD_DB=1"
)

REM Update containers selectively
if defined REBUILD_MAIN (
    if defined REBUILD_DB (
        echo Building and updating all Docker containers...
        docker-compose up -d --build
    ) else (
        echo Building and updating main backend container only...
        docker-compose up -d --build backend-api
    )
) else if defined REBUILD_DB (
    echo Building and updating database container only...
    docker-compose up -d --build mysql
) else (
    echo Starting existing containers without rebuild...
    docker-compose up -d
)

if errorlevel 1 (
    echo [ERROR] Failed to update Docker containers
    cd ..
    exit /b 1
)
echo [OK] Docker containers updated successfully

REM Backend is already started in Docker container

REM ===== START SECOND BACKEND (LARAVEL + MONGODB) =====
echo Starting second backend (Laravel + MongoDB)...
cd ..\backend-api-second\develop
docker-compose up -d --build
if %errorlevel% equ 0 (
    echo [OK] Second backend containers updated successfully
) else (
    echo [ERROR] Failed to update second backend containers
    cd ..\..\..
    exit /b 1
)

REM Second backend started (no health check wait)
echo [OK] Second backend update initiated
cd ..\..\..

REM ===== HEALTH CHECK =====
echo Waiting for backend...
set /a attempt=0
set /a max_attempts=20

:health_check_loop
set /a attempt+=1
if !attempt! gtr !max_attempts! (
    echo [ERROR] Backend update failed after !max_attempts! attempts
    echo [ERROR] Check backend logs for details
    exit /b 1
)

echo Health check !attempt!/!max_attempts!...
netstat -ano | findstr /R /C:":!BACKEND_PORT! .*LISTENING" >nul 2>&1
if !errorlevel! equ 0 (
    echo [OK] Backend ready after %attempt% attempts
    goto start_frontend
)

goto health_check_loop

:start_frontend
echo Starting frontend...

REM Debug: Show URLs before opening
echo [DEBUG] Backend URL: !HEALTH_URL!
echo [DEBUG] Frontend URL: !FRONTEND_URL!

echo Opening backend health page in browser...
start "Backend-Health" "!HEALTH_URL!"

cd "%FRONTEND_DIR%"

REM Check if frontend is already running on the expected port
netstat -ano | findstr ":%FRONTEND_PORT% " | findstr LISTENING >nul
if !errorlevel! equ 0 (
    echo Frontend is already running on port %FRONTEND_PORT%
) else (
    echo Starting frontend development server on port %FRONTEND_PORT%
    start "Frontend" cmd /c "npm run dev"
    timeout /t 5 >nul
)

REM Open the application in browser
echo Opening frontend application in browser...
start "App" "!FRONTEND_URL!"

echo.
echo ============================================
echo SUCCESS: %APP_NAME% updated successfully!
echo ============================================
echo Backend: !HEALTH_URL!
echo Frontend: !FRONTEND_URL!
echo.
