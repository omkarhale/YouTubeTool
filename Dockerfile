# ================================
# STAGE 1 : BUILD
# ================================
# We use Maven + Java 21 to compile
# your code and create a JAR file
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set a working directory inside
# the container (like a project folder)
WORKDIR /app

# Copy pom.xml first
# Why? Docker caches dependencies separately
# So if only your code changes (not pom.xml),
# Docker won't re-download all dependencies again
# This saves a LOT of time on rebuilds
COPY pom.xml .

# Download all Maven dependencies
RUN mvn dependency:go-offline -B

# Now copy your actual source code
COPY src ./src

# Build the JAR file, skip tests for faster build
RUN mvn clean package -DskipTests


# ================================
# STAGE 2 : RUN
# ================================
# We use lightweight Java 21 JRE
# (just to RUN the app, no Maven needed)
# This keeps the final image small (~150MB)
# instead of heavy Maven image (~500MB)
# ================================
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy only the built JAR from Stage 1
# We don't copy source code — just the final output
COPY --from=build /app/target/YouTubeTool-0.0.1-SNAPSHOT.jar app.jar

# Tell Docker this container uses port 8080
EXPOSE 8080

# Command to run your Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]