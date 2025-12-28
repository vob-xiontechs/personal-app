@echo off
REM Default profile is 'develop' if not provided
SET PROFILE=%1
IF "%PROFILE%"=="" SET PROFILE=develop

echo Running Spring Boot with profile: %PROFILE%

REM Run Spring Boot with mvnw.cmd
mvnw.cmd "spring-boot:run" "-Dspring-boot.run.profiles=%PROFILE%"
