# Build stage: Compile and package Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies to optimize layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and package jar
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage: Lightweight JRE 17 container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/student-outpass-management-*.jar app.jar

# Render assigns port dynamically via PORT env variable (defaults to 8080)
ENV PORT=8080
EXPOSE ${PORT}

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
