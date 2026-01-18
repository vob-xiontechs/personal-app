# Personal App Startup Guide

## Overview

This system is designed to ensure the Spring Boot backend is fully started before the frontend begins running. This prevents API connection errors when the frontend tries to communicate with a backend that isn't ready.

## Changes Made

### 1. MySQL Connection Fix
- Added `allowPublicKeyRetrieval=true` to JDBC URL to fix "Public Key Retrieval is not allowed" error
- Updated file: `backend-api/src/main/resources/application-develop.yml`

### 2. Docker Compose Improvements
- Added health check for MySQL container
- Backend service now waits for MySQL to be healthy before starting
- Updated file: `docker/develop/docker-compose.yml`

### 3. Spring Boot Actuator Addition
- Added `spring-boot-starter-actuator` dependency to `backend-api/pom.xml`
- Configured health endpoint to check backend status
- Updated file: `backend-api/src/main/resources/application-develop.yml`

### 4. Temporary Security Disable
- Disabled Spring Security in develop environment for easy testing
- Configuration: `spring.security.enabled=false`

### 5. Layered Exception Handling
- Implemented comprehensive exception hierarchy for different layers
- BaseException with errorCode and layer identification
- Specific exceptions: ServiceException, BusinessException, RepositoryException, etc.
- Enhanced error responses with layer information for better debugging

### 6. Swagger/OpenAPI Documentation
- Added springdoc-openapi-starter-webmvc-ui for API documentation
- Configured Swagger UI with custom branding and server information
- Added comprehensive API annotations for testing

## How to Use

### Windows (Batch script)
```bash
.\start.bat
```

### Linux/Mac (Shell script)
```bash
./start.sh
```

## Startup Process

1. **Start backend services**: Docker Compose starts MySQL and Spring Boot
2. **Wait for MySQL healthy**: Script waits for MySQL database to be ready (may take 5-10 seconds)
3. **Wait for backend ready**: Script checks backend health endpoint `/actuator/health`
4. **Start frontend**: When backend is fully ready, frontend starts

## Ports

- **Backend API**: http://localhost:8081
- **Frontend**: http://localhost:3011 (or next available port)
- **MySQL**: localhost:3307 (external), container port 3306

## API Documentation

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

## Health Check Endpoints

- **Backend Health**: http://localhost:8081/actuator/health

## Notes

- Frontend will only start after backend is completely ready
- If backend doesn't start within 5 minutes, script will report error
- Security is temporarily disabled in develop environment for easy testing
- When deploying to production, security needs to be re-enabled and properly configured

## Database Backup

The system includes automated database backup functionality to preserve data before container deletion.

### Automatic Backup During Startup

When you run `start.bat`, the system automatically:
1. **Checks for running database**: Detects if database containers are currently running
2. **Creates backup**: If running containers found, automatically backs up the database
3. **Continues startup**: Proceeds with stopping containers and starting fresh ones

### Manual Backup

You can also run backup manually at any time:

#### Manual Docker Command
```bash
# For develop environment
cd docker/develop
docker-compose --profile backup up db-backup

# For staging environment
cd docker/staging
docker-compose --profile backup up db-backup
```

### Backup Process

1. **Dump Database**: Uses `mysqldump` to create a SQL file with timestamp
2. **Save Locally**: Stores backup in `docker/{env}/backup/` directory
3. **Manual Git Commit**: Backup files are saved locally and can be manually committed to git if needed

### Backup Files

- Location: `docker/develop/backup/` or `docker/staging/backup/`
- Naming: `backup_YYYYMMDD_HHMMSS.sql`
- Gitignored: Backup files are excluded from version control (only the directory structure)

### Important Notes

- Run backup **before** stopping containers to ensure latest data is saved
- Backup service runs independently and doesn't affect running containers
- If git push fails, backup files are still saved locally

## Troubleshooting

### Backend Won't Start
- Check Docker containers: `docker ps`
- View logs: `docker logs backend-develop`
- Check MySQL: `docker logs db-develop`

### Frontend Can't Connect to Backend
- Ensure backend health endpoint returns status "UP"
- Check firewall and port conflicts

### MySQL Connection Error
- Ensure MySQL container is healthy: `docker ps`
- Verify credentials in `application-develop.yml`

### Backup Issues
- Ensure database container is running: `docker ps`
- Check backup logs: `docker logs db-backup-develop`
- Verify backup directory permissions
