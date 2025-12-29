@echo off
REM Run Unit Tests for the Backend API
echo Running Unit Tests for Backend API...

REM Run ProfileServiceImplTest only
echo Running ProfileServiceImplTest...
mvnw.cmd test -Dtest="ProfileServiceImplTest"

echo Unit tests completed.
pause
