# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS build

# Set the volume for the temporary directory
VOLUME /tmp

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests


# Stage 2: Create the final image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]