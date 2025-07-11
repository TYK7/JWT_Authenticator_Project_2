# Spring Boot Project Migration Prompt from Java 1.8 to Java 21

This document outlines the requirements for migrating a Spring Boot project from Java 1.8 to Java 21. The project includes JWT-based authentication, email functionality (e.g., for password reset or notifications), database connectivity (using Spring Data JPA), and Google OAuth2 authentication for social login. The migration accounts for the project’s previous downgrade from Java 17 to Java 1.8 and aims to ensure a smooth transition to Java 21, maintaining all existing functionality while adopting modern Java features, updated dependencies, and best practices for a secure, scalable, and maintainable application.

---

## Project Overview

- **Current State**:
  - Java version: 1.8
  - Framework: Spring Boot (likely version 1.x, compatible with Java 1.8)
  - Features:
    - JWT authentication for secure API access with user and role management.
    - Email functionality for sending notifications or password reset links.
    - Database connectivity using Spring Data JPA with a database (e.g., H2, MySQL, or PostgreSQL).
    - Google OAuth2 authentication for social login integration.
  - Build tool: Maven
  - Context: Previously migrated from Java 17 to Java 1.8, indicating familiarity with newer Java versions and potential compatibility issues.

- **Goal**:
  - Upgrade to Java 21 (Long-Term Support version) and Spring Boot 3.x (compatible with Java 17+).
  - Ensure all existing features (JWT, email, database, Google OAuth2) remain fully functional.
  - Address deprecated APIs, update dependencies, and leverage Java 21 features (e.g., records, text blocks, switch expressions) where applicable.
  - Enhance security, performance, and maintainability while maintaining compatibility with the existing architecture.

---

## Migration Requirements

### 1. Pre-Migration Preparation

- **Backup and Version Control**:
  - Commit the current Java 1.8 project to a version control system (e.g., Git) to preserve the existing state.
  - Create a dedicated branch (e.g., `migration-java-21`) for the migration process.

- **Dependency Review**:
  - Analyze the current `pom.xml` to identify dependencies related to Spring Boot, Spring Security, JWT, Spring Data JPA, email functionality, and Google OAuth2.
  - Note any custom or third-party libraries that may require updates or replacements.

- **Environment Setup**:
  - Install Java 21 (e.g., OpenJDK 21 or Adoptium Temurin 21) on the development environment.
  - Update the IDE (e.g., IntelliJ IDEA, Eclipse) to support Java 21 and its features.
  - Ensure the Maven build tool is updated to a recent version (e.g., 3.9.x) for compatibility with Java 21 and Spring Boot 3.x.

---

### 2. Update Maven Configuration

- **Update Java Version**:
  - Configure the Maven project to target Java 21 by setting the appropriate properties for the source and target versions.
  - Ensure the Maven compiler plugin is updated to support Java 21 compilation.

- **Upgrade Spring Boot**:
  - Update the Spring Boot parent dependency to version 3.x (e.g., 3.2.0), which is compatible with Java 17 and above.
  - Verify that all Spring-related dependencies (e.g., `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-mail`) align with the new Spring Boot version.

- **Update Dependencies**:
  - Replace outdated dependencies with versions compatible with Java 21 and Spring Boot 3.x:
    - Use the latest `jjwt` library for JWT handling, ensuring it supports modern Java versions.
    - Update database drivers (e.g., H2 or PostgreSQL) to the latest compatible versions.
    - Ensure `spring-security-oauth2-client` and `spring-security-oauth2-jose` are included for Google OAuth2 support.
    - Optionally, include `lombok` to reduce boilerplate code, ensuring the latest version is used.
  - Remove or replace any deprecated or incompatible libraries identified during the review.

---

### 3. Update Existing Features for Java 21 Compatibility

- **JWT Authentication**:
  - Review the existing JWT implementation, which likely uses an older `jjwt` library version for token generation, validation, and claim extraction.
  - Update to the latest `jjwt` version, ensuring compatibility with Java 21 and Spring Security 5.x.
  - Refactor the JWT utility to handle token generation with secure algorithms (e.g., HS256 or RS256) and validate tokens using modern APIs.
  - Update the JWT authentication filter to integrate with Spring Security’s filter chain, ensuring compatibility with the updated security configuration.
  - Consider using Java 21 records for immutable data structures (e.g., for JWT response objects).

- **Email Functionality**:
  - Verify the existing email functionality, which likely uses `spring-boot-starter-mail` with `JavaMailSender` for sending emails.
  - Update the email configuration to align with Spring Boot 3.x and Java 21, ensuring compatibility with modern JavaMail properties.
  - Enhance email templates using Java 21 text blocks for cleaner formatting of email content (e.g., password reset links).
  - Ensure secure email configuration (e.g., using TLS with SMTP servers like Gmail).

- **Database Connectivity**:
  - Review the existing database setup, likely using Spring Data JPA with a database like H2 or MySQL.
  - Update entity classes to use `jakarta.persistence` APIs (replacing `javax.persistence`) as required by Spring Boot 3.x.
  - Verify database configuration in `application.properties` or `application.yml` to ensure compatibility with the chosen database driver and Spring Data JPA.
  - If schema migrations are needed, consider integrating a migration tool like Flyway or Liquibase for automated database updates.

- **Google OAuth2 Authentication**:
  - Review the existing Google OAuth2 setup, likely using `spring-security-oauth2-client` for social login.
  - Update the OAuth2 configuration to align with Spring Security 5.x and Java 21, ensuring proper integration with `jakarta.servlet` APIs.
  - Verify Google OAuth2 client credentials (client ID, client secret) and update `application.properties` with the latest scope definitions (e.g., `openid`, `email`, `profile`).
  - Ensure the OAuth2 login flow redirects correctly and integrates with the JWT-based authentication system.

---

### 4. Leverage Java 21 Features

- **Records**:
  - Use records for immutable data transfer objects (DTOs) in API responses (e.g., for JWT tokens, user details) to reduce boilerplate and improve readability.

- **Text Blocks**:
  - Apply text blocks for multi-line strings in email templates or JSON payloads to enhance code clarity and maintainability.

- **Switch Expressions**:
  - Refactor conditional logic (e.g., role-based checks) to use switch expressions for more concise and expressive code.

- **Virtual Threads (Project Loom)**:
  - Explore virtual threads for improved concurrency in high-throughput operations, such as sending emails or querying the database, to enhance performance.

- **Sealed Classes (Optional)**:
  - Consider using sealed classes for modeling restricted hierarchies (e.g., user roles or error types) to improve type safety.

---

### 5. Security and Compliance

- **Security Updates**:
  - Update Spring Security to version 5.x (included in Spring Boot 3.x) to leverage modern security features.
  - Ensure the JWT authentication filter validates tokens securely and integrates with the Spring Security context.
  - Verify that password encoding uses `BCryptPasswordEncoder` for secure storage.

- **Compliance**:
  - Ensure email functionality complies with data protection regulations (e.g., GDPR, CCPA) by securing user data in transit.
  - Validate input data (e.g., URLs, user inputs) to prevent injection attacks.
  - Check that Google OAuth2 flows adhere to Google’s API policies.

---

### 6. Testing and Validation

- **Unit Testing**:
  - Update unit tests to use JUnit 5, which is standard in Spring Boot 3.x.
  - Test JWT generation/validation, email sending, database operations, and OAuth2 flows.
  - Ensure tests cover edge cases (e.g., invalid tokens, expired tokens, database errors).

- **Integration Testing**:
  - Perform end-to-end tests for critical flows (e.g., signup → login → OAuth2 authentication).
  - Use tools like `TestRestTemplate` for API testing and mock SMTP servers (e.g., GreenMail) for email testing.

- **Manual Testing**:
  - Verify JWT authentication (login, token validation).
  - Test email functionality with a real or mock SMTP server.
  - Confirm Google OAuth2 login redirects and integrates correctly.
  - Validate database operations (e.g., CRUD) remain unaffected.

---

### 7. Additional Features and Enhancements

- **Refresh Tokens**:
  - Add support for refresh tokens to maintain user sessions without frequent logins.
  - Store refresh tokens securely in the database and include them in login responses.

- **Email Verification**:
  - Implement email confirmation during signup to verify user accounts using a secure token.

- **Rate Limiting**:
  - Apply rate limiting to authentication endpoints to prevent abuse, using a library like Bucket4j or Spring Cloud Gateway.

- **Audit Logging**:
  - Log critical user actions (e.g., login attempts, password resets) to a database or file for security auditing.

- **API Versioning**:
  - Introduce versioned API endpoints (e.g., `/v1/auth`) to support future updates without breaking existing clients.

- **Performance Optimization**:
  - Leverage Java 21 virtual threads for concurrent tasks (e.g., email sending, database queries) to improve scalability.
  - Optimize database queries using Spring Data JPA’s query optimization features.

---

### 8. Post-Migration Steps

- **Code Review**:
  - Check for deprecated APIs (e.g., `javax.*` replaced with `jakarta.*`).
  - Ensure all imports are updated to reflect Java 21 and Spring Boot 3.x dependencies.
  - Validate that no Java 1.8-specific workarounds remain.

- **Build and Deployment**:
  - Test the build process with `mvn clean install` to ensure compatibility.
  - Deploy to a development environment and verify all features function as expected.
  - Consider containerization with Docker for consistent deployment.

- **Documentation**:
  - Update project documentation to reflect the new Java 21 and Spring Boot 3.x setup.
  - Document any new features (e.g., refresh tokens, email verification) and configuration changes.

- **Performance Testing**:
  - Evaluate performance improvements from Java 21’s enhanced garbage collection and virtual threads.
  - Benchmark critical operations (e.g., JWT validation, database queries) to ensure no regressions.

---

This prompt ensures a structured and comprehensive migration of your Spring Boot project from Java 1.8 to Java 21, preserving existing functionality (JWT, email, database, Google OAuth2) while enhancing security, performance, and maintainability. It is tailored to account for the previous downgrade from Java 17 and provides a clear path to adopting modern Java features.