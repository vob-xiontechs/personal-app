@echo off
REM ============================================
REM Backup Before Cleanup Script
REM Creates database backup before any container/image/volume cleanup
REM ============================================

echo Starting backup before cleanup operations...

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
    set "ENV_DISPLAY=DEVELOP"
) else if "%ENVIRONMENT%"=="staging" (
    set "DOCKER_DIR=docker\staging"
    set "ENV_DISPLAY=STAGING"
) else (
    set "DOCKER_DIR=docker\develop"
    set "ENV_DISPLAY=UNKNOWN"
)

echo ============================================
echo ENVIRONMENT: %ENV_DISPLAY%
echo OPERATION: BACKUP BEFORE CLEANUP
echo ============================================

cd "%DOCKER_DIR%"

REM Check if database container is running
docker ps --filter "name=db-%ENVIRONMENT%" --filter "status=running" -q >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Database container is running, creating backup...
) else (
    echo [WARNING] Database container is not running, backup may not contain current data
)

REM Create backup
echo [INFO] Creating database backup...
docker-compose --profile backup up db-backup

if %errorlevel% equ 0 (
    echo [SUCCESS] Backup completed successfully
    REM Show the created backup file
    for /f %%i in ('dir /b /o-d backup\backup_%ENVIRONMENT%_*.sql 2^>nul') do (
        echo [INFO] Backup file created: %%i
        goto :backup_done
    )
    echo [WARNING] Backup command succeeded but file not found
) else (
    echo [ERROR] Backup failed with error code %errorlevel%
    exit /b 1
)

:backup_done
cd ..\..
echo ============================================
echo Backup before cleanup completed!
echo You can now safely proceed with cleanup operations.
echo ============================================
