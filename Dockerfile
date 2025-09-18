# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the fat JAR produced by Spring Boot (matches *-SNAPSHOT.jar or *-RELEASE.jar)
COPY --from=build /workspace/target/*.jar /app/app.jar

# Expose Spring Boot port
EXPOSE 8080

# Memory-friendly JVM defaults for Render free instance
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
