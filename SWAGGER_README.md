# Swagger API Documentation

This JWT Authenticator project now includes comprehensive Swagger/OpenAPI documentation for all REST endpoints.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Features

### 🔐 Authentication Endpoints
- **POST /auth/register** - Register a new user account
- **POST /auth/login** - User login with credentials
- **POST /auth/google** - Google OAuth2 sign-in with ID token
- **POST /auth/token** - Generate JWT authentication tokens
- **POST /auth/refresh** - Refresh JWT tokens
- **GET /auth/verify-email** - Verify email address with token

### 🔑 Two-Factor Authentication Endpoints
- **POST /auth/tfa/setup** - Generate 2FA secret and QR code
- **POST /auth/tfa/verify** - Verify TOTP code
- **POST /auth/tfa/enable** - Enable 2FA for user
- **POST /auth/tfa/disable** - Disable 2FA for user

### 📋 API Documentation Features
- **Interactive Testing** - Try out endpoints directly from the UI
- **Request/Response Examples** - See sample data for all endpoints
- **Schema Documentation** - Detailed model definitions
- **Security Configuration** - JWT Bearer token authentication
- **Multi-tenant Support** - Documented tenant ID requirements

### 🛡️ Security
- JWT Bearer token authentication is configured
- Use the "Authorize" button in Swagger UI to set your Bearer token
- Format: `Bearer your-jwt-token-here`

## How to Use

1. **Start the Application**
   ```bash
   mvn spring-boot:run
   ```

2. **Open Swagger UI**
   - Navigate to http://localhost:8080/swagger-ui.html

3. **Register a User**
   - Use the `/auth/register` endpoint to create a new account
   - Example payload:
   ```json
   {
     "username": "john_doe",
     "password": "SecurePassword123!",
     "email": "john.doe@example.com",
     "tenantId": "tenant1",
     "location": "New York, USA"
   }
   ```

4. **Login and Get Token**
   - Use the `/auth/login` endpoint to authenticate
   - Copy the JWT token from the response

5. **Authorize in Swagger**
   - Click the "Authorize" button in Swagger UI
   - Enter: `Bearer your-jwt-token-here`
   - Now you can test protected endpoints

## Configuration

The Swagger configuration can be customized in:
- `src/main/java/com/example/jwtauthenticator/config/SwaggerConfig.java`
- `src/main/resources/application.properties`

### Current Swagger Settings
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
```

## API Overview

The API is organized into the following sections:

### Authentication
- User registration and login
- Google OAuth2 sign-in integration
- JWT token management
- Email verification
- Password reset functionality
- Two-factor authentication (2FA) with TOTP

### Security Features
- Multi-tenant architecture
- Email verification required
- JWT access and refresh tokens
- Audit logging
- CORS configuration

## Models

Key data models documented in Swagger:
- **RegisterRequest** - User registration data
- **AuthRequest** - Login credentials
- **GoogleSignInRequest** - Google ID token for OAuth2 sign-in
- **AuthResponse** - JWT token response
- **PasswordResetRequest** - Password reset data
- **TfaRequest** - Two-factor authentication data
- **TfaSetupResponse** - 2FA setup with secret and QR code

## Development

To modify the API documentation:
1. Update annotations in controller classes
2. Modify model classes with `@Schema` annotations
3. Update the `SwaggerConfig.java` for global settings
4. Restart the application to see changes