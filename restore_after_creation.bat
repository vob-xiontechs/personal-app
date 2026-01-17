@echo off
REM ============================================
REM Restore After Creation Script
REM Restores database from the most recent backup after containers are created
REM ============================================

echo Starting database restoration after container creation...

REM Set default environment
set "DEFAULT_ENV=develop"
set "ENV_FILE=frontend-client\.env"

REM Read environment from config file
for /f "tokens=1,2 delims==" %%a in (%ENV_FILE%) do (
    if "%%a"=="ENVIRONMENT" set "ENVIRONMENT=%%b"
)

REM Set defaults
if "%ENVIRONMENT%"=="" set "ENVIRONMENT=%DEFAULT_ENV%"

REM Configure environment
if "%ENVIRONMENT%"=="develop" (
    set "DOCKER_DIR=docker\develop"
    set "BACKEND_PORT=8081"
    set "ENV_DISPLAY=DEVELOP"
) else if "%ENVIRONMENT%"=="staging" (
    set "DOCKER_DIR=docker\staging"
    set "BACKEND_PORT=8082"
    set "ENV_DISPLAY=STAGING"
) else (
    set "DOCKER_DIR=docker\develop"
    set "BACKEND_PORT=8081"
    set "ENV_DISPLAY=UNKNOWN"
)

echo ============================================
echo ENVIRONMENT: %ENV_DISPLAY%
echo OPERATION: RESTORE FROM LATEST BACKUP
echo ============================================

cd "%DOCKER_DIR%"

REM Check if database container is running
docker ps --filter "name=db-%ENVIRONMENT%" --filter "status=running" -q >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Database container is running
) else (
    echo [ERROR] Database container is not running. Please ensure containers are started first.
    cd ..\..
    exit /b 1
)

REM Check for existing backup files
if exist "backup\backup_%ENVIRONMENT%_*.sql" (
    echo [INFO] Found existing backup files
    REM Show the latest backup file
    for /f %%i in ('dir /b /o-d backup\backup_%ENVIRONMENT%_*.sql 2^>nul') do (
        echo [INFO] Latest backup file: %%i
        goto :restore_backup
    )
) else (
    echo [WARNING] No backup files found for environment %ENVIRONMENT%
    echo [INFO] Database will remain empty or with schema only
    cd ..\..
    goto :no_backup_found
)

:restore_backup
REM Wait for backend to be healthy (schema should be created by Flyway)
echo [INFO] Waiting for backend to be ready before restoring data...
set "HEALTH_URL=http://localhost:%BACKEND_PORT%/actuator/health"
set /a "attempt=0"
set /a "max_attempts=30"

:health_check_loop
set /a "attempt+=1"
if %attempt% gtr %max_attempts% (
    echo [ERROR] Backend startup failed after %max_attempts% attempts
    echo [ERROR] Cannot proceed with database restoration
    cd ..\..
    exit /b 1
)

echo Health check %attempt%/%max_attempts%...
curl -s --max-time 3 "%HEALTH_URL%" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Backend ready, proceeding with database restoration
    goto :proceed_with_restore
)

timeout /t 2 >nul
goto health_check_loop

:proceed_with_restore
REM Restore from the latest backup
echo [INFO] Restoring database from latest backup...
docker-compose --profile init up db-init

if %errorlevel% equ 0 (
    echo [SUCCESS] Database restoration completed successfully
) else (
    echo [ERROR] Database restoration failed with error code %errorlevel%
    cd ..\..
    exit /b 1
)

cd ..\..
echo ============================================
echo Database restoration completed successfully!
echo ============================================
goto :end

:no_backup_found
echo ============================================
echo No backup files found - database remains with schema only
echo ============================================

:end
