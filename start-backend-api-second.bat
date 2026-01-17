@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

REM ==================================================
REM Project root (where backend-api-second folder lives)
REM ==================================================
set PROJECT_ROOT=D:\Work\app-demo\personal-app

REM ==================================================
REM Docker compose file path
REM ==================================================
set COMPOSE_FILE=%PROJECT_ROOT%\docker\backend-api-second\develop\docker-compose.yml

echo ================================================
echo Starting Backend API Second Development
echo ================================================
echo.

REM ==================================================
REM Validate paths (anti silent fail)
REM ==================================================
if not exist "%PROJECT_ROOT%\backend-api-second\Dockerfile" (
    echo ERROR: Dockerfile not found:
    echo %PROJECT_ROOT%\backend-api-second\Dockerfile
    pause
    exit /b 1
)

if not exist "%COMPOSE_FILE%" (
    echo ERROR: docker-compose.yml not found:
    echo %COMPOSE_FILE%
    pause
    exit /b 1
)

REM ==================================================
REM Move to project root
REM ==================================================
cd /d "%PROJECT_ROOT%"

echo Using project root:
echo %CD%
echo.

REM ==================================================
REM Stop containers
REM ==================================================
echo Stopping existing containers...
docker-compose -f "%COMPOSE_FILE%" down
echo.

REM ==================================================
REM Build
REM ==================================================
echo Building containers (no cache)...
docker-compose -f "%COMPOSE_FILE%" build --no-cache
if errorlevel 1 (
    echo.
    echo BUILD FAILED - check docker-compose build context
    pause
    exit /b 1
)
echo.

REM ==================================================
REM Run
REM ==================================================
echo Starting containers...
docker-compose -f "%COMPOSE_FILE%" up -d
if errorlevel 1 (
    echo.
    echo START FAILED
    pause
    exit /b 1
)
echo.

echo ================================================
echo Backend API Second is now running!
echo ================================================
echo - Laravel app : http://localhost:8010
echo - MongoDB     : localhost:27017
echo.
echo To stop services:
echo docker-compose -f "%COMPOSE_FILE%" down
echo.

pause
ENDLOCAL
