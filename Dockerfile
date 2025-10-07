# Stage 1: Define the base image for running the Java application
FROM eclipse-temurin:17-jdk-jammy

# Stage 2: Configuration

# Set the working directory to /app. This is where subsequent commands (like the ENTRYPOINT)
# will look for files if a relative path is used.
WORKDIR /app

# Copy the executable JAR from the local 'target' directory into the container's WORKDIR (/app).
# We rename the long versioned file (admin-0.0.1-SNAPSHOT.jar) to the simpler 'app.jar'.
# The file location is now /app/app.jar.
COPY target/admin-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port (default for Spring Boot)
EXPOSE 8080

# Stage 3: Execution

# Define the command to run the application.
# Since WORKDIR is /app, we can use the relative filename "app.jar".
# The full command executed is: java -jar /app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]