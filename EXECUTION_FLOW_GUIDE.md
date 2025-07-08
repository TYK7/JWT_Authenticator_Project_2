# JWT Authenticator Project - Complete Execution Flow Guide

## 🚀 Application Startup Flow

### 1. Application Bootstrap
```
JwtAuthenticatorApplication.main() 
    ↓
SpringApplication.run() 
    ↓
Spring Boot Auto-Configuration
    ↓
Component Scanning & Bean Creation
    ↓
Database Connection (PostgreSQL)
    ↓
Security Configuration Loading
    ↓
Tomcat Server Start (Port 8080)
```

**Key Components Initialized:**
- `@SpringBootApplication` - Main application class
- `@EnableAspectJAutoProxy` - Enables AOP for auditing
- Database connection to AWS RDS PostgreSQL
- Security filter chain setup
- JWT utilities and services
- Swagger/OpenAPI documentation

---

## 🔐 Authentication & Authorization Flow

### 2. User Registration Flow
```
POST /auth/register
    ↓
AuthController.registerUser()
    ↓
AuthService.registerUser()
    ↓
1. Check username/email uniqueness (per tenant)
2. Encode password (BCrypt)
3. Generate verification token (UUID)
4. Save user to database (email_verified = false)
5. Send verification email
    ↓
EmailService.sendVerificationEmail()
    ↓
Return success message
```

**Database Operations:**
- Insert new User entity with verification token
- User status: `email_verified = false`

### 3. Email Verification Flow
```
GET /auth/verify-email?token={verification_token}
    ↓
AuthController.verifyEmail()
    ↓
AuthService.verifyEmail()
    ↓
1. Find user by verification token
2. Update email_verified = true
3. Clear verification token
4. Save user
    ↓
Return "Email verified successfully"
```

### 4. User Login Flow
```
POST /auth/login
    ↓
AuthController.loginUser()
    ↓
AuthService.loginUser()
    ↓
1. Check if email is verified
2. Authenticate credentials
    ↓
AuthenticationManager.authenticate()
    ↓
JwtUserDetailsService.loadUserByUsernameAndTenantId()
    ↓
3. Generate JWT tokens
    ↓
JwtUtil.generateToken() & JwtUtil.generateRefreshToken()
    ↓
4. Save refresh token to database
5. Log login event to login_log table
6. Return AuthResponse with tokens
```

### 4.1. Google Sign-In Flow
```
POST /auth/google
    ↓
AuthController.googleSignIn()
    ↓
AuthService.googleSignIn()
    ↓
1. Verify Google ID token
    ↓
GoogleTokenVerificationService.verifyIdToken()
    ↓
2. Extract user info from Google token
3. Check if user exists by email
    ↓
If user exists:
    - Update user info (profile picture, etc.)
    - Set emailVerified = true
    - Update refresh token
If user doesn't exist:
    - Create new user with emailVerified = true
    - Generate unique username
    - Set authProvider = GOOGLE
    ↓
4. Generate JWT tokens
5. Log login event (method = GOOGLE)
6. Return AuthResponse with tokens
```

**JWT Token Structure:**
- **Access Token**: Short-lived (15 minutes), contains user details
- **Refresh Token**: Long-lived (7 days), used to generate new access tokens

### 5. Protected Endpoint Access Flow
```
Request to Protected Endpoint (with Authorization header)
    ↓
JwtRequestFilter.doFilterInternal()
    ↓
1. Extract JWT from "Authorization: Bearer {token}"
2. Extract X-Tenant-Id header
3. Validate tenant ID presence
4. Extract username from JWT
    ↓
JwtUtil.extractUsername()
    ↓
5. Load user details with tenant context
    ↓
JwtUserDetailsService.loadUserByUsernameAndTenantId()
    ↓
6. Validate JWT token
    ↓
JwtUtil.validateToken()
    ↓
7. Set authentication in SecurityContext
8. Continue to controller method
```

---

## 🔄 Token Management Flow

### 6. Token Refresh Flow
```
POST /auth/refresh
    ↓
AuthController.refreshToken()
    ↓
AuthService.refreshToken()
    ↓
1. Validate refresh token
2. Extract username from refresh token
3. Load user details
4. Generate new access & refresh tokens
5. Update refresh token in database
6. Return new tokens
```

---

## 🔒 Two-Factor Authentication (2FA) Flow

### 7. 2FA Setup Flow
```
POST /auth/tfa/setup?username={username}
    ↓
AuthController.setupTfa()
    ↓
TfaService.generateNewSecret()
    ↓
1. Generate TOTP secret using GoogleAuth
2. Save secret to user record
3. Return secret for QR code generation
```

### 8. 2FA Verification Flow
```
POST /auth/tfa/verify
    ↓
AuthController.verifyTfa()
    ↓
TfaService.verifyCode()
    ↓
1. Get user's TFA secret
2. Verify TOTP code using GoogleAuth
3. Return verification result
```

### 9. 2FA Enable/Disable Flow
```
POST /auth/tfa/enable?username={username}
    ↓
TfaService.enableTfa()
    ↓
Update user.tfaEnabled = true

POST /auth/tfa/disable?username={username}
    ↓
TfaService.disableTfa()
    ↓
Update user.tfaEnabled = false
```

---

## 🔑 Password Reset Flow

### 10. Forgot Password Flow
```
POST /auth/forgot-password
    ↓
AuthController.forgotPassword()
    ↓
PasswordResetService.createPasswordResetToken()
    ↓
1. Find user by email
2. Generate reset token (UUID)
3. Save PasswordResetToken entity (expires in 1 hour)
4. Send reset email with token
    ↓
EmailService.sendPasswordResetEmail()
```

### 11. Password Reset Confirmation Flow
```
POST /auth/reset-password
    ↓
AuthController.resetPassword()
    ↓
PasswordResetService.resetPassword()
    ↓
1. Find valid reset token (not expired)
2. Get associated user
3. Encode new password
4. Update user password
5. Delete reset token
6. Return success message
```

---

## 🛡️ Security Architecture

### 12. Security Filter Chain
```
HTTP Request
    ↓
DisableEncodeUrlFilter
    ↓
WebAsyncManagerIntegrationFilter
    ↓
SecurityContextPersistenceFilter
    ↓
HeaderWriterFilter
    ↓
CorsFilter
    ↓
LogoutFilter
    ↓
JwtRequestFilter (Custom)
    ↓
RequestCacheAwareFilter
    ↓
SecurityContextHolderAwareRequestFilter
    ↓
AnonymousAuthenticationFilter
    ↓
SessionManagementFilter
    ↓
ExceptionTranslationFilter
    ↓
FilterSecurityInterceptor
    ↓
Controller Method
```

### 13. Multi-Tenant Architecture
- **Tenant Isolation**: Each request requires `X-Tenant-Id` header
- **Data Separation**: Users are isolated by tenant ID
- **Security Context**: Authentication includes tenant validation

---

## 📊 Auditing & Monitoring Flow

### 14. Audit Logging Flow
```
Controller Method Execution
    ↓
@AuditLog Annotation Detected
    ↓
AuditAspect.logAuditEvent() (AOP)
    ↓
AuditService.logEvent()
    ↓
1. Extract user information
2. Log action, timestamp, IP address
3. Store audit record
```

**Audit Information Captured:**
- User ID and username
- Action performed
- Timestamp
- IP address
- Request details

---

## 🔄 Request Forwarding Flow

### 15. Authenticated Request Forwarding
```
POST /auth/forward (with X-Forward-URL header)
    ↓
AuthController.forwardRequest()
    ↓
1. Authenticate user credentials
2. Generate JWT token
3. Create HTTP headers with Authorization
4. Forward request to target URL using RestTemplate
5. Return response from target service
```

---

## 📋 API Endpoints Summary

### Authentication Endpoints
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/register` | User registration | No |
| GET | `/auth/verify-email` | Email verification | No |
| POST | `/auth/login` | User login | No |
| POST | `/auth/google` | Google OAuth2 sign-in | No |
| POST | `/auth/token` | Generate token | No |
| POST | `/auth/refresh` | Refresh token | No |

### Password Management
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/forgot-password` | Request password reset | No |
| POST | `/auth/reset-password` | Confirm password reset | No |

### Two-Factor Authentication
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/tfa/setup` | Setup 2FA | No |
| POST | `/auth/tfa/verify` | Verify 2FA code | No |
| POST | `/auth/tfa/enable` | Enable 2FA | No |
| POST | `/auth/tfa/disable` | Disable 2FA | No |

### Utility Endpoints
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/forward` | Forward authenticated request | No |
| GET | `/protected/**` | Protected resources | Yes |

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BINARY(16) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    location VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    refresh_token TEXT,
    tfa_secret VARCHAR(255),
    tfa_enabled BOOLEAN DEFAULT FALSE,
    tenant_id VARCHAR(255),
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    auth_provider VARCHAR(50) DEFAULT 'LOCAL',
    profile_picture_url VARCHAR(500)
);
```

### Login Log Table
```sql
CREATE TABLE login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    login_method VARCHAR(50) NOT NULL,
    login_status VARCHAR(50) NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);
```

### Password Reset Tokens Table
```sql
CREATE TABLE password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BINARY(16) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

---

## 🔧 Configuration Files

### Application Properties
- `application.properties` - Main configuration
- `application-postgres.properties` - Database configuration
- JWT secret configuration
- Email service configuration
- Swagger/OpenAPI settings

### Security Configuration
- JWT token expiration times
- Password encoding (BCrypt)
- CORS configuration
- Security filter chain setup

---

## 🚨 Error Handling Flow

### Global Exception Handling
```
Exception Thrown in Controller/Service
    ↓
GlobalExceptionHandler.handleException()
    ↓
1. Log error details
2. Determine appropriate HTTP status
3. Create standardized error response
4. Return error response to client
```

**Common Exception Types:**
- `BadCredentialsException` - Invalid login credentials
- `RuntimeException` - Business logic errors
- `ValidationException` - Input validation errors
- `JwtException` - Token-related errors

---

## 📖 API Documentation

### Swagger/OpenAPI Integration
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`
- **Features**: Interactive API testing, request/response examples

---

## 🔍 Monitoring & Health Checks

### Spring Boot Actuator
- **Health Check**: `http://localhost:8080/actuator/health`
- **Application Info**: `http://localhost:8080/actuator/info`
- **Metrics**: Available through actuator endpoints

---

## 🎯 Key Features Summary

1. **Multi-tenant Architecture** - Tenant-based user isolation
2. **JWT Authentication** - Stateless token-based auth
3. **Email Verification** - Secure account activation
4. **Two-Factor Authentication** - TOTP-based 2FA
5. **Password Reset** - Secure password recovery
6. **Request Forwarding** - Authenticated proxy functionality
7. **Audit Logging** - Comprehensive action tracking
8. **API Documentation** - Interactive Swagger UI
9. **Global Exception Handling** - Standardized error responses
10. **Database Integration** - PostgreSQL with JPA/Hibernate

This execution flow guide provides a complete understanding of how each feature works and how they interact with each other in the JWT Authenticator project.