@echo off
REM ============================================
REM Rebuild Laravel Backend Script
REM Rebuilds only the backend-api-second container
REM ============================================

echo Rebuilding Laravel backend container...

REM Navigate to the Laravel docker directory
cd backend-api-second\develop

REM Stop the current container
echo Stopping current Laravel container...
docker-compose down

REM Rebuild and restart
echo Rebuilding Laravel container...
docker-compose up -d --build

REM Return to project root
cd ..\..

echo.
echo ============================================
echo Laravel backend rebuilt successfully!
echo Visit: http://localhost:8010
echo ============================================
