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
    set "DOCKER_DIR=docker\backend-api"
    set "ENV_DISPLAY=DEVELOP"
) else if "%ENVIRONMENT%"=="staging" (
    set "DOCKER_DIR=docker\backend-api\staging"
    set "ENV_DISPLAY=STAGING"
) else (
    set "DOCKER_DIR=docker\backend-api"
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
    echo [SUCCESS] Database backup completed successfully
    REM Show the created backup file
    for /f %%i in ('dir /b /o-d backup\backup_%ENVIRONMENT%_*.sql 2^>nul') do (
        echo [INFO] Database backup file created: %%i
        goto :db_backup_done
    )
    echo [WARNING] Database backup command succeeded but file not found
) else (
    echo [ERROR] Database backup failed with error code %errorlevel%
    exit /b 1
)

:db_backup_done
REM Create environment files backup
echo [INFO] Creating environment files backup...
set "TIMESTAMP=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=%TIMESTAMP: =0%"

if not exist "backup\env" mkdir "backup\env"

REM Backup main frontend .env
if exist "..\..\..\frontend-client\.env" (
    copy "..\..\..\frontend-client\.env" "backup\env\.env_frontend_%ENVIRONMENT%_%TIMESTAMP%" >nul
    echo [INFO] Frontend .env backed up
)

REM Backup backend .env if exists
if exist "..\..\..\backend-api\.env" (
    copy "..\..\..\backend-api\.env" "backup\env\.env_backend_%ENVIRONMENT%_%TIMESTAMP%" >nul
    echo [INFO] Backend .env backed up
)

REM Backup second backend .env
if exist "..\..\..\backend-api-second\.env" (
    copy "..\..\..\backend-api-second\.env" "backup\env\.env_backend_second_%ENVIRONMENT%_%TIMESTAMP%" >nul
    echo [INFO] Second backend .env backed up
)

echo [SUCCESS] Environment files backup completed

:backup_done
cd ..\..
echo ============================================
echo Backup before cleanup completed!
echo You can now safely proceed with cleanup operations.
echo ============================================
