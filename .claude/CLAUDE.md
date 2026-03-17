# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the **rebook-auth-service**, a Spring Boot 3.3.13 microservice handling authentication and authorization for the Rebook platform. It provides traditional username/password authentication and OAuth2 integration (Google, Naver, Kakao), with JWT-based session management and Redis caching.

**Key Technologies:**
- Java 17
- Spring Boot 3.3.13 with Spring Security, Spring Data JPA, Spring Cloud (Eureka, OpenFeign)
- PostgreSQL database
- Redis cache
- JWT authentication (jjwt 0.12.5)
- Gradle build system

## Common Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application (dev profile by default)
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with coverage report (generated at build/reports/jacoco/test/html/index.html)
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests com.example.rebookauthservice.YourTestClass

# Run specific test method
./gradlew test --tests com.example.rebookauthservice.YourTestClass.testMethod
```

### Code Quality
```bash
# Run all verification tasks (tests + coverage)
./gradlew check
```

### Packaging
```bash
# Create executable JAR
./gradlew bootJar

# Build OCI container image
./gradlew bootBuildImage
```

## Architecture Overview

### Package Structure
```
com.example.rebookauthservice/
├── controller/          # AuthController, PassportController
├── service/             # AuthService, PassportService + OAuth (Factory pattern: Google/Naver/Kakao)
├── model/               # DTOs (request/response) + Entity (AuthUser)
├── repository/          # AuthRepository (JPA)
├── clients/             # Feign clients (UserClient, NotificationClient)
├── config/              # Security, Redis, Swagger, Passport configs
├── utils/               # JwtUtil, RedisUtil
└── exception/           # Custom exceptions + GlobalExceptionHandler
```

### Key Patterns
- **OAuth Factory**: `OAuthServiceFactory` dynamically routes to provider-specific services (Google/Naver/Kakao) via `@OAuthServiceType` annotation
- **JWT Authentication**: Access tokens (30 min) + Refresh tokens (7 days) stored in Redis
- **Feign Integration**: UserClient communicates with user-service, all services registered with Eureka

## API Endpoints

### Authentication Endpoints (`/api/auth/**`)
All authentication endpoints are publicly accessible (whitelisted in SecurityConfig):

- **POST `/api/auth/signup`**: Register new user with username/password
  - Body: `SignUpRequest` (username, password)
  - Returns: `CommonResult` (success/failure)

- **POST `/api/auth/login`**: Traditional username/password login
  - Body: `LoginRequest` (username, password)
  - Returns: `SingleResult<TokenResponse>` (access token, refresh token)

- **POST `/api/auth/oauth/{provider}`**: OAuth social login
  - Path variable: `provider` (GOOGLE, NAVER, KAKAO)
  - Body: `OAuthRequest` (authorization code)
  - Returns: `SingleResult<TokenResponse>` (access token, refresh token)

- **POST `/api/auth/refresh`**: Refresh access token
  - Body: `RefreshRequest` (refresh token)
  - Returns: `SingleResult<RefreshResponse>` (new access token)

- **GET `/api/auth/test`**: Health check endpoint
  - Returns: Simple "test success" message

### Passport Endpoints (`/api/passport/**`)
Requires authentication (JWT token):

- **POST `/api/passport`**: Issue passport (authentication verification)
  - Returns: Passport information

## Configuration Files

### Environment-Specific Configuration
- `application.yaml`: Base configuration, sets active profile to 'dev'
- `application-dev.yaml`: Development settings with Spring Cloud Config integration
  - Config server: `http://rebook-config:8888`
  - Falls back to local config if Config Server unavailable (optional:configserver)

### Key Configuration Properties
- **JWT secrets**: Configured via Spring Cloud Config Server (access-secret, refresh-secret)
- **Token validity**: access-validity (30 min), refresh-validity (7 days)
- **Database**: PostgreSQL with Hibernate auto-update (dev), should use validate/none in prod
- **Redis**: Used for refresh token storage with password authentication
- **Eureka**: Service registration enabled with service discovery
- **Spring Cloud Config**: Centralized configuration management

## Important Development Notes

### Security Considerations
- JWT secrets managed via Spring Cloud Config Server - ensure proper secret management in production
- Current SecurityConfig whitelists `/api/auth/**` - all other endpoints require JWT authentication
- Stateless session management (SessionCreationPolicy.STATELESS)
- CSRF disabled (appropriate for stateless JWT-based API)
- Password validation enforced via `@Password` annotation and `PasswordValidator`
- OAuth tokens are exchanged server-side to protect client secrets
- Refresh tokens stored in Redis with expiration matching token validity

### Database Schema
- JPA entity auditing enabled (@EnableJpaAuditing in main class)
- Hibernate ddl-auto set to "update" in dev - change to "validate" or "none" for production
- PostgreSQL dialect configured

### Testing
- JaCoCo configured for test coverage reporting
- HTML reports generated at `build/reports/jacoco/test/html/index.html`
- Tests run automatically before coverage report generation

### OAuth Provider Integration
When adding a new OAuth provider:
1. Create provider-specific DTOs in `model/dto/oauth/{provider}/`
2. Implement service extending `AbstractOAuthService`
3. Annotate with `@OAuthServiceType(Provider.YOUR_PROVIDER)`
4. Add provider to `Provider` enum
5. Factory will auto-register the service on startup

### Monitoring
- Spring Boot Actuator enabled with all endpoints exposed (management.endpoints.web.exposure.include=*)
- Consider restricting actuator endpoints in production
- Swagger/OpenAPI documentation enabled at default path (`/swagger-ui.html`)
- Prometheus metrics available via Micrometer registry

### GitHub Packages Authentication
The project depends on a custom library (`passport-common`) hosted on GitHub Packages. To build the project, you must:

1. Set environment variables:
   ```bash
   export GITHUB_ACTOR=your-github-username
   export GITHUB_TOKEN=your-personal-access-token
   ```

2. Ensure your GitHub Personal Access Token has `read:packages` permission

3. The build will fail without these credentials when trying to resolve `com.rebook:passport-common:1.0.0`

Alternative: If you have the library locally, place it in the `libs/` directory (flatDir repository is configured)

## Dependencies to Note

### Core Dependencies
- **Spring Boot 3.3.13**: Core framework
- **Spring Cloud 2023.0.5**: Eureka client, OpenFeign, Config client for microservices
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database ORM layer
- **Spring Data Redis**: Redis integration for caching

### Database & Caching
- **PostgreSQL**: Primary database (runtime dependency)
- **Redis**: Refresh token caching and session management

### Authentication & Security
- **JJWT 0.12.5**: JWT token generation and validation
  - `jjwt-api`: API interfaces
  - `jjwt-impl`: Implementation (runtime)
  - `jjwt-jackson`: JSON serialization (runtime)

### Monitoring & Observability
- **Sentry 8.13.2**: Error tracking and monitoring (with Spring Boot Jakarta starter)
- **Spring Boot Actuator**: Health checks and metrics
- **Micrometer Prometheus**: Metrics registry for Prometheus integration

### API Documentation
- **Springdoc OpenAPI 2.6.0**: Swagger UI and OpenAPI 3 documentation

### Communication & Serialization
- **Protobuf 3.25.2**: Protocol Buffers for efficient serialization
- **Protobuf Gradle Plugin 0.9.4**: Build-time code generation

### Custom Libraries
- **passport-common 1.0.0**: Shared library from GitHub Packages
  - Requires GITHUB_ACTOR and GITHUB_TOKEN environment variables for authentication
  - Repository: `https://maven.pkg.github.com/gratisreise/passport-common`

### Development Tools
- **Lombok**: Reduce boilerplate code (compileOnly)
- **Spring Boot DevTools**: Hot reload and development utilities (developmentOnly)
- **JaCoCo**: Code coverage reporting

### Testing
- **Spring Boot Starter Test**: Comprehensive testing support
- **Spring Rabbit Test**: RabbitMQ testing utilities
- **JUnit Platform Launcher**: Test execution (runtime)
