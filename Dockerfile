# Multi-stage build for optimized Docker image
# Build stage - Use official Maven image with OpenJDK 8
FROM maven:3.6.3-openjdk-8-slim AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first (for better caching)
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Production stage
FROM openjdk:8-jre-alpine

# Install curl for health checks
RUN apk --no-cache add curl

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the jar file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check - Use PORT environment variable
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Environment variables optimized for Render's 512MB limit
ENV JAVA_OPTS="-Xmx350m -Xms128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -Djava.awt.headless=true" \
    SPRING_PROFILES_ACTIVE=postgres

# Run the application - Render will set PORT environment variable
CMD ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]