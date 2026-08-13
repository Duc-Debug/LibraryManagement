# Stage 1: Build the application using Maven and Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies to utilize Docker cache layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image with Temurin JRE 21 Alpine
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create app directory, uploads folder and non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    mkdir -p /app/uploads && \
    chown -R appuser:appgroup /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup /app/app.jar

USER appuser

EXPOSE 8080

# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy Maven POM & download dependencies for Docker Layer Caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy Source code & Build production JAR
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from builder stage
COPY --from=builder /app/target/LibraryManagement-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables default
ENV SPRING_PROFILES_ACTIVE=prod

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
