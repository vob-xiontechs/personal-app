# Personal App 📱

A full-stack web application built with Clean Architecture, featuring user profile management with modern UI/UX design.

## 🏗️ Architecture

This project follows Clean Architecture principles with separate layers:

- **Domain Layer**: Business logic and entities
- **Application Layer**: Use cases and application logic
- **Infrastructure Layer**: External services (HTTP, Database)
- **Presentation Layer**: React components and UI

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software
- **Node.js** (v18 or higher) - [Download](https://nodejs.org/)
- **npm** or **yarn** - Comes with Node.js
- **Java** (JDK 17 or higher) - [Download](https://adoptium.net/)
- **Maven** (v3.6 or higher) - [Download](https://maven.apache.org/)
- **MySQL** (v8.0 or higher) - [Download](https://dev.mysql.com/downloads/mysql/)
- **Git** - [Download](https://git-scm.com/)

### Optional (for development)
- **Docker** - For containerized deployment
- **VS Code** - Recommended IDE with extensions
- **Postman** - For API testing

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/vob-xiontechs/personal-app.git
cd personal-app
```

### 2. Backend Setup (Java Spring Boot)

#### Prerequisites for Backend
- Java JDK 17+
- Maven 3.6+
- MySQL 8.0+

#### MySQL Setup

First, install and start MySQL server:

**Windows:**
```bash
# Download and install MySQL from https://dev.mysql.com/downloads/mysql/
# Or use chocolatey: choco install mysql
```

**macOS (using Homebrew):**
```bash
brew install mysql
brew services start mysql
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

**Create Database:**
```sql
-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE testdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional, you can use root)
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON testdb.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### Environment Variables

Create `.env` file in `backend-api/` directory:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=testdb
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Or set environment variables:

**Windows:**
```cmd
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=testdb
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
```

**Linux/macOS:**
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=testdb
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

#### Docker Environment Setup

If using Docker Compose, the database connection info will be:

```bash
# Docker Environment Variables
DB_HOST=mysql
DB_PORT=3306
DB_NAME=testdb
DB_USERNAME=appuser
DB_PASSWORD=apppassword
```

**Database Connection Details:**
- **Host**: `mysql` (Docker service name) or `localhost:3306` (from host)
- **Database**: `testdb`
- **Username**: `appuser`
- **Password**: `apppassword`
- **Root Password**: `root` (for admin access)

#### Setup Steps

```bash
# Navigate to backend directory
cd backend-api

# Install dependencies and build
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

**Alternative using Maven directly:**
```bash
# If you have Maven installed globally
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup (React + TypeScript)

#### Prerequisites for Frontend
- Node.js 18+
- npm or yarn

#### Setup Steps

```bash
# Navigate to frontend directory
cd frontend-client

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:3009`

### 4. Access the Application

- **Frontend**: http://localhost:3009
- **Backend API**: http://localhost:8080
- **MySQL Database**: Connect using your MySQL client

## 🐳 Docker Setup (Alternative)

### Using Docker Compose

For development with MySQL in Docker:

```yaml
# docker-compose.yml (create in root directory)
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: personal-app-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: testdb
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password

  backend:
    build: ./backend-api
    container_name: personal-app-backend
    ports:
      - "8080:8080"
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: testdb
      DB_USERNAME: appuser
      DB_PASSWORD: apppassword
    depends_on:
      - mysql

  frontend:
    build: ./frontend-client
    container_name: personal-app-frontend
    ports:
      - "3009:3009"
    depends_on:
      - backend

volumes:
  mysql_data:
```

```bash
# Run all services
docker-compose up --build
```

### Individual Docker Builds

#### MySQL Docker
```bash
docker run --name personal-app-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=testdb \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppassword \
  -p 3306:3306 \
  -d mysql:8.0
```

#### Backend Docker
```bash
cd backend-api
docker build -t personal-app-backend .
docker run --name personal-app-backend \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=testdb \
  -e DB_USERNAME=appuser \
  -e DB_PASSWORD=apppassword \
  -p 8080:8080 \
  personal-app-backend
```

#### Frontend Docker
```bash
cd frontend-client
docker build -t personal-app-frontend .
docker run --name personal-app-frontend \
  -p 3009:3009 \
  personal-app-frontend
```

## 🧪 Testing

### Frontend Testing

```bash
cd frontend-client

# Run tests
npm test

# Run tests with coverage
npm run test:coverage
```

### Backend Testing

```bash
cd backend-api

# Run tests
./mvnw test

# Run integration tests
./mvnw verify
```

## 📁 Project Structure

```
personal-app/
├── backend-api/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/java/          # Java source code
│   │   └── main/resources/     # Configuration files
│   │       ├── application.yml # MySQL configuration
│   │       └── db/migration/   # Flyway migrations
│   └── pom.xml                 # Maven configuration
├── frontend-client/             # React frontend
│   ├── src/
│   │   ├── application/        # Application layer (Use Cases)
│   │   ├── domain/             # Domain layer (Entities, Services)
│   │   ├── infrastructure/     # Infrastructure layer (HTTP, Config)
│   │   ├── mocks/              # Mock data for testing
│   │   ├── presentation/       # Presentation layer (Components)
│   │   └── shared/             # Shared utilities
│   ├── package.json
│   └── vite.config.ts
├── docker/                      # Docker configurations
└── README.md
```

## 🔧 Configuration

### Environment Variables

#### Frontend (.env)
```bash
VITE_API_BASE_URL=http://localhost:8080
```

#### Backend Environment Variables
```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=testdb
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# Application
SERVER_PORT=8080
```

#### Backend (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:testdb}?useSSL=false&serverTimezone=UTC

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

  flyway:
    enabled: true
    locations: classpath:db/migration
```

## 📡 API Endpoints

### Profile Management
- `POST /api/profile` - Create new profile
- `GET /api/profiles` - Get all profiles
- `GET /api/profiles/{id}` - Get profile by ID
- `PUT /api/profiles/{id}` - Update profile
- `DELETE /api/profiles/{id}` - Delete profile

## 🧪 Mock Data Testing

The frontend includes mock data for testing without a backend:

### Test Scenarios
- **Success**: `test@example.com` - Creates profile successfully
- **Email Exists**: `test@error.com` - Returns "Email already exists"
- **Server Error**: `test@server.com` - Returns "Server error occurred"

### Running Mock Tests

```bash
cd frontend-client

# Run mock data tests
npm run test:mocks
```

## 🛠️ Development Scripts

### Frontend Scripts
```bash
cd frontend-client

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run tests
npm run test

# Lint code
npm run lint

# Format code
npm run format
```

### Backend Scripts
```bash
cd backend-api

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Package application
./mvnw package

# Clean and rebuild
./mvnw clean install
```

## 🚀 Deployment

### Frontend Deployment
```bash
cd frontend-client
npm run build
# Deploy the 'dist' folder to your web server
```

### Backend Deployment
```bash
cd backend-api
./mvnw clean package -DskipTests
# Deploy the JAR file to your server
java -jar target/*.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Troubleshooting

### Common Issues

**Frontend won't start:**
- Ensure Node.js 18+ is installed
- Try deleting `node_modules` and running `npm install` again
- Check if port 3009 is available

**Backend won't start:**
- Ensure Java 17+ and Maven are installed
- Check MySQL is running and credentials are correct
- Verify environment variables are set
- Check if port 8080 is available

**Database connection issues:**
- Ensure MySQL is running: `sudo systemctl status mysql`
- Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`
- Check connection string in application.yml
- Test connection: `mysql -h localhost -u root -p testdb`

**MySQL timezone issues:**
- Add `?serverTimezone=UTC` to JDBC URL
- Or set MySQL timezone: `SET GLOBAL time_zone = '+00:00';`

### MySQL Specific Issues

**Authentication plugin issues:**
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
FLUSH PRIVILEGES;
```

**Access denied for user:**
```sql
GRANT ALL PRIVILEGES ON testdb.* TO 'username'@'localhost' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```

**Port already in use:**
```bash
# Find process using port 3306
sudo lsof -i :3306
# Kill the process or change MySQL port in my.cnf
```

### Getting Help

- Check the Issues section on GitHub
- Review the code documentation in each module
- Test with mock data first to isolate issues
- Check MySQL error logs: `/var/log/mysql/error.log`

---

**Happy coding! 🎉**
