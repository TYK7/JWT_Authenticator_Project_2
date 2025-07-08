# JWT Authenticator Project

This project implements a JWT-based authentication and authorization microservice using Spring Boot.

## Functional Requirements

### REST APIs

*   `POST /auth/register`: Register a new user.
*   `POST /auth/token`: Validate user credentials and return a JWT access token.
*   `POST /auth/login`: Authenticate user, then internally call `/auth/token`, and return the token.
*   `POST /auth/google`: Authenticate user with Google OAuth2 ID token.
*   `POST /auth/forward`: After successful login, forward the request to a configurable external/internal URL.
*   `GET /api/protected`: A secured endpoint accessible only with a valid JWT.

### User Entity Fields

*   `userId` (UUID)
*   `username`
*   `password` (encrypted using BCrypt)
*   `email` (with format validation)
*   `location` (city, state, or general string)
*   `role` (USER, ADMIN)
*   `authProvider` (LOCAL, GOOGLE)
*   `emailVerified` (boolean)
*   `profilePictureUrl` (for Google users)

## Technical & Security Features

*   JWT utility class for parsing, signing, and validation.
*   BCrypt for password encoding.
*   Global CORS configuration.
*   Secure HTTP headers (X-Frame-Options, X-XSS-Protection, Strict-Transport-Security, etc.).
*   CSRF disabled for stateless endpoints.
*   HTTPS-ready setup.
*   Input validation using `@Valid`, `@Email`, `@NotBlank`, etc.

## Advanced Features

*   Refresh Token Support
*   Email Verification during registration
*   Password Reset via email with token
*   Two-Factor Authentication (2FA) with TOTP
*   Google OAuth2 Sign-In Integration
*   Multi-Tenancy (basic discriminator field or schema separation)
*   Audit Logging with Login Tracking

## Build & Test

*   Use Maven to build and package as executable `.jar` via `spring-boot-maven-plugin`.
*   Include unit tests (e.g., for services, JWT utils).
*   Include integration tests (e.g., auth/login/register).
*   The project must be reusable as an auth module in other microservices.

## Extras

*   Swagger/OpenAPI documentation.
*   Postman collection with comprehensive API testing.
*   Google OAuth2 integration with demo page.
*   Complete API testing guide and documentation.

## Setup Instructions

1.  **Prerequisites**:
    *   Java 11 or higher
    *   Maven 3.6+

2.  **Clone the repository**:
    ```bash
    git clone <repository_url>
    cd jwt-authenticator
    ```

3.  **Build the project**:
    ```bash
    mvn clean install
    ```

4.  **Run the application**:
    ```bash
    java -jar target/jwt-authenticator-0.0.1-SNAPSHOT.jar
    ```
    The application will start on port 8080 by default.

## API Usage

### Swagger/OpenAPI Documentation

Access the API documentation at: `http://localhost:8080/swagger-ui.html`

### Example cURL Commands

*   **Register User**:
    ```bash
    curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"username": "testuser", "password": "password", "email": "test@example.com", "location": "New York"}'
    ```

*   **Login and Get Token**:
    ```bash
    curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"username": "testuser", "password": "password"}'
    ```

*   **Google Sign-In**:
    ```bash
    curl -X POST http://localhost:8080/auth/google -H "Content-Type: application/json" -d '{"idToken": "YOUR_GOOGLE_ID_TOKEN"}'
    ```

*   **Access Protected Endpoint** (replace `YOUR_ACCESS_TOKEN` with the token obtained from login):
    ```bash
    curl -X GET http://localhost:8080/api/protected -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
    ```

*   **Refresh Token**:
    ```bash
    curl -X POST http://localhost:8080/auth/refresh -H "Content-Type: application/json" -d '{"refreshToken": "YOUR_REFRESH_TOKEN"}'
    ```

*   **Forgot Password**:
    ```bash
    curl -X POST http://localhost:8080/auth/forgot-password -H "Content-Type: application/json" -d '{"email": "test@example.com"}'
    ```

*   **Reset Password** (use the token from the email):
    ```bash
    curl -X POST "http://localhost:8080/auth/reset-password?token=YOUR_RESET_TOKEN" -H "Content-Type: application/json" -d '{"newPassword": "new_password"}'
    ```

*   **Setup 2FA**:
    ```bash
    curl -X POST "http://localhost:8080/auth/tfa/setup?username=testuser"
    ```

*   **Verify 2FA Code**:
    ```bash
    curl -X POST http://localhost:8080/auth/tfa/verify -H "Content-Type: application/json" -d '{"username": "testuser", "code": "123456"}'
    ```

*   **Enable 2FA**:
    ```bash
    curl -X POST "http://localhost:8080/auth/tfa/enable?username=testuser"
    ```

*   **Disable 2FA**:
    ```bash
    curl -X POST "http://localhost:8080/auth/tfa/disable?username=testuser"
    ```

*   **Forward Request** (replace `YOUR_ACCESS_TOKEN` and `YOUR_USER_ID`):
    ```bash
    curl -X POST http://localhost:8080/auth/forward -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_ACCESS_TOKEN" -H "userId: YOUR_USER_ID" -d '{"key": "value"}'
    ```

## Database Profiles

The application uses an in-memory H2 database by default. You can configure other databases by modifying `application.properties`.

## Token Flow Explanation

1.  **Registration (`/auth/register`)**: A new user registers with username, password, email, and location. An email verification token is sent to the provided email address.
2.  **Email Verification (`/auth/verify-email`)**: The user clicks the link in the email to verify their account.
3.  **Login (`/auth/login`)**: The user provides credentials. If valid, an access token (short-lived) and a refresh token (long-lived) are returned.
4.  **Google Sign-In (`/auth/google`)**: Users can sign in with their Google account using an ID token. Google users have their email automatically verified.
5.  **Access Protected Resources (`/api/protected`)**: The access token is sent in the `Authorization` header (`Bearer <token>`) to access protected endpoints.
6.  **Token Refresh (`/auth/refresh`)**: When the access token expires, the refresh token can be used to obtain a new access token and a new refresh token.
7.  **Password Reset (`/auth/forgot-password`, `/auth/reset-password`)**: Users can request a password reset via email. A token is sent to their email, which can then be used to set a new password.
8.  **Two-Factor Authentication (`/auth/tfa/setup`, `/auth/tfa/verify`, `/auth/tfa/enable`, `/auth/tfa/disable`)**: Users can enable 2FA for an added layer of security. During login, if 2FA is enabled, a separate verification step is required.

## Forwarding Logic with RestTemplate/WebClient

The `/auth/forward` endpoint demonstrates how to forward requests to another service. It uses `RestTemplate` (default) and can be configured to use `WebClient` (commented out in the code). The target URL for forwarding is configured via the `forward.url` property in `application.properties`.

## Reusability as an Auth Module

This project is designed to be reusable as an authentication and authorization module in other microservices. You can include it as a dependency in your other Spring Boot projects.

## 📚 Documentation

### Core Documentation
- **README.md** - Main project documentation
- **EXECUTION_FLOW_GUIDE.md** - Complete execution flow and architecture
- **API_TESTING_GUIDE.md** - Comprehensive API testing guide
- **SWAGGER_README.md** - Swagger/OpenAPI documentation guide

### Google OAuth2 Integration
- **GOOGLE_OAUTH_SETUP.md** - Google OAuth2 setup and configuration
- **GOOGLE_SIGNIN_POSTMAN_REQUESTS.json** - Additional Postman requests for Google Sign-In

### API Testing
- **JWT_Authenticator_Postman_Collection.json** - Main Postman collection
- **JWT_Authenticator_Environment.json** - Postman environment variables
- **UPDATE_POSTMAN_COLLECTION.md** - Instructions to update Postman collection

### Quick Links
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Google Sign-In Demo**: http://localhost:8080/test/google-signin-demo
- **Health Check**: http://localhost:8080/actuator/health
