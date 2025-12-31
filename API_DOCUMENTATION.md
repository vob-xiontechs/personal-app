# Personal App Backend API Documentation

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Data Models](#data-models)
- [Error Handling](#error-handling)
- [Authentication](#authentication)
- [Examples](#examples)
- [Versioning](#versioning)
- [Extensibility](#extensibility)

## 📖 Overview

### Introduction

Personal App Backend API is a comprehensive RESTful web service built with Spring Boot 3.4.1 that provides user profile management functionality. The API follows modern architectural patterns including layered exception handling, comprehensive documentation, and scalable design principles.

### Key Features

- **User Profile Management**: Complete CRUD operations for user profiles
- **Layered Exception Handling**: Structured error management across all application layers
- **Interactive Documentation**: Swagger UI and OpenAPI 3.0 specification
- **GraphQL Support**: Alternative query interface for flexible data retrieval
- **Health Monitoring**: Comprehensive health checks and metrics
- **Docker Containerization**: Production-ready containerized deployment

### Technology Stack

- **Framework**: Spring Boot 3.4.1
- **Language**: Java 21
- **Database**: MySQL 8.0
- **ORM**: Hibernate/JPA
- **Documentation**: SpringDoc OpenAPI 3.0
- **Build Tool**: Maven 3.9+
- **Container**: Docker & Docker Compose

## 🏗️ Architecture

### Layered Architecture

The application follows a clean layered architecture pattern:

```
┌─────────────────┐
│   Controller    │ ← HTTP Request/Response Handling
├─────────────────┤
│    Service      │ ← Business Logic & Validation
├─────────────────┤
│  Repository     │ ← Data Access & Persistence
├─────────────────┤
│    Entity       │ ← Data Models & Relationships
└─────────────────┘
```

#### Controller Layer
- **ProfileController**: Handles user profile related HTTP requests
- **HomeController**: Provides welcome page and API information
- **GlobalExceptionHandler**: Centralized exception handling
- **CustomErrorController**: Custom error page handling

#### Service Layer
- **ProfileService**: Business logic for profile operations
- **ProfileServiceImpl**: Implementation with validation and business rules

#### Repository Layer
- **UserRepository**: JPA repository for UserEntity operations
- Database migration management via Flyway

#### Entity Layer
- **UserEntity**: JPA entity representing user data
- Database schema management with constraints and relationships

### Exception Hierarchy

The application implements a comprehensive exception handling strategy:

```
BaseException (Abstract)
├── ControllerException    - Controller layer errors
├── ServiceException       - Service layer errors
├── BusinessException      - Business logic errors
├── RepositoryException    - Data access errors
├── ValidationException    - Input validation errors
├── EntityException        - Entity/model errors
└── InfrastructureException- Infrastructure errors
```

## 🔗 API Endpoints

### Base URL
```
http://localhost:8081
```

### Authentication
Currently, authentication is disabled in development environment. Add Spring Security for production deployment.

### Endpoints Overview

| Method | Endpoint | Description | Status |
|--------|----------|-------------|---------|
| GET | `/` | Welcome page | ✅ Active |
| POST | `/api/v1.0/register` | User registration | ✅ Active |
| POST | `/graphql` | GraphQL endpoint | ✅ Active |
| GET | `/graphiql` | GraphQL IDE | ✅ Active |
| GET | `/actuator/health` | Health check | ✅ Active |
| GET | `/swagger-ui.html` | API documentation | ✅ Active |
| GET | `/v3/api-docs` | OpenAPI specification | ✅ Active |

## 📊 Data Models

### User Profile Registration

#### ProfileRequest
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

**Field Specifications:**
- `name`: String (3-50 characters, required)
- `email`: String (valid email format, required, unique)
- `password`: String (8-128 characters, required)

#### ProfileResponse
```json
{
  "userId": "string",
  "name": "string",
  "email": "string",
  "isAccountVerified": "boolean"
}
```

**Field Specifications:**
- `userId`: UUID string (auto-generated)
- `name`: String (user's full name)
- `email`: String (user's email address)
- `isAccountVerified`: Boolean (verification status)

#### UserEntity (Database Model)
```java
@Entity
@Table(name = "tbl_users")
public class UserEntity {
    @Id
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean isAccountVerified;
    private Long resetOtpExpireAt;
    private String verifyOtp;
    private Long verifyOtpExpireAt;
    private String resetOtp;
}
```

## 🚨 Error Handling

### Error Response Format

All API errors follow a consistent JSON structure:

```json
{
  "error": "string",
  "type": "string",
  "status": "number"
}
```

For layered exceptions, additional information is provided:

```json
{
  "error": "string",
  "type": "string",
  "layer": "string",
  "status": "number"
}
```

### HTTP Status Codes

| Status Code | Description | Common Usage |
|-------------|-------------|--------------|
| 200 | OK | Successful GET requests |
| 201 | Created | Successful POST/PUT resource creation |
| 400 | Bad Request | Validation errors, malformed requests |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resources (e.g., email exists) |
| 415 | Unsupported Media Type | Invalid content type |
| 500 | Internal Server Error | Server errors, unexpected exceptions |

### Error Types by Layer

#### Business Layer Errors
- `BUSINESS_ERROR`: Business rule violations
- Example: Email already registered

#### Validation Layer Errors
- `VALIDATION_ERROR`: Input validation failures
- `fieldErrors`: Detailed field-level validation errors

#### Repository Layer Errors
- `REPOSITORY_ERROR`: Database operation failures
- Connection issues, constraint violations

#### Controller Layer Errors
- `CONTROLLER_ERROR`: Request processing errors
- Invalid request format, missing parameters

## 🔐 Authentication

### Current Implementation
Authentication is temporarily disabled in the development environment (`spring.security.enabled=false`).

### Future Implementation (Recommended)
For production deployment, implement Spring Security with:

1. **JWT Token Authentication**
2. **OAuth2 Integration**
3. **Role-based Access Control (RBAC)**
4. **Password Encryption (BCrypt)**

### Security Configuration Template
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1.0/register").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
```

## 💡 Examples

### User Registration

#### Request
```bash
curl -X POST http://localhost:8081/api/v1.0/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "SecurePass123"
  }'
```

#### Success Response (201)
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "isAccountVerified": false
}
```

#### Error Response (409)
```json
{
  "error": "Email already exists",
  "type": "BUSINESS_ERROR",
  "layer": "BUSINESS",
  "status": 400
}
```

### Health Check

#### Request
```bash
curl http://localhost:8081/actuator/health
```

#### Response
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000,
        "free": 300000000,
        "threshold": 10000000
      }
    }
  }
}
```

## 🔢 Versioning

### API Versioning Strategy

The API uses URI versioning (v1.0) for clear version identification:

```
/api/v1.0/register
/api/v1.0/profiles
/api/v1.0/profiles/{id}
```

### Semantic Versioning

- **MAJOR.MINOR.PATCH** (e.g., v1.0.0)
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Version Headers

```http
Accept: application/vnd.personalapp.v1+json
X-API-Version: 1.0
```

## 🚀 Extensibility

### Adding New Endpoints

1. **Controller Layer**: Create new controller with proper annotations
```java
@RestController
@RequestMapping("/api/v1.0/profiles")
@Tag(name = "User Profiles", description = "Profile management operations")
public class ProfileController {
    // Implementation
}
```

2. **Service Layer**: Implement business logic
```java
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    // Implementation
}
```

3. **Repository Layer**: Data access operations
```java
@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
    // Custom queries
}
```

### Adding New Exception Types

Extend the BaseException hierarchy:

```java
public class CustomBusinessException extends BaseException {
    public CustomBusinessException(String message) {
        super(message, "CUSTOM_BUSINESS_ERROR", "BUSINESS");
    }
}
```

### Database Schema Evolution

Use Flyway migrations for schema changes:

```sql
-- V2__add_profile_picture.sql
ALTER TABLE tbl_users
ADD COLUMN profile_picture_url VARCHAR(500);
```

### Monitoring and Metrics

Extend actuator endpoints for custom metrics:

```java
@Configuration
public class MetricsConfig {
    @Bean
    public MeterBinder customMetrics() {
        return registry -> {
            // Custom metrics implementation
        };
    }
}
```

## 📈 Performance Considerations

### Database Optimization
- Connection pooling (HikariCP)
- Proper indexing on frequently queried columns
- Query optimization with JPA Criteria API

### Caching Strategy
- Redis integration for session management
- Caffeine for application-level caching
- HTTP caching headers for static resources

### Asynchronous Processing
- `@Async` for non-blocking operations
- Message queues for background tasks
- Event-driven architecture for scalability

## 🧪 Testing Strategy

### Unit Testing
- Service layer testing with Mockito
- Repository testing with @DataJpaTest
- Controller testing with MockMvc

### Integration Testing
- Full application context testing
- Database integration tests
- API endpoint testing

### Performance Testing
- JMeter for load testing
- Database query performance monitoring
- Memory leak detection

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [REST API Design Best Practices](https://restfulapi.net/)

## 🤝 Contributing

1. Follow the established architectural patterns
2. Add comprehensive tests for new features
3. Update API documentation for endpoint changes
4. Maintain backward compatibility for API changes
5. Use proper commit messages and PR descriptions

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Last Updated**: December 31, 2025
**API Version**: 1.0.0
**Spring Boot Version**: 3.4.1
