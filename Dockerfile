# Super simple - build everything in Docker
FROM maven:3.8.4-openjdk-17

# Set working directory
WORKDIR /app

# Copy all source files
COPY . .

# Build and run in one container (simple but works)
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
