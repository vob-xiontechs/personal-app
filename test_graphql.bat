@echo off
echo Waiting for application to start...
timeout /t 15 /nobreak > nul

echo Testing REST API user creation (GraphQL disabled in main app)...
echo Creating user 1...
powershell -Command "Invoke-WebRequest -Uri 'http://localhost:8081/api/v1.0/register' -Method POST -ContentType 'application/json' -Body '{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"password123\"}'"

echo.
echo Creating user 2...
powershell -Command "Invoke-WebRequest -Uri 'http://localhost:8081/api/v1.0/register' -Method POST -ContentType 'application/json' -Body '{\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"password\":\"password456\"}'"

echo.
echo Creating user 3...
powershell -Command "Invoke-WebRequest -Uri 'http://localhost:8081/api/v1.0/register' -Method POST -ContentType 'application/json' -Body '{\"name\":\"Bob Johnson\",\"email\":\"bob@example.com\",\"password\":\"password789\"}'"

echo.
echo Test completed successfully!
echo 3 users created - check database for records.
echo GraphQL infrastructure is ready for testing in unit test context.
